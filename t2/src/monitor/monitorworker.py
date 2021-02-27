import os
import datetime
import re
import time
import jsonlines
import json
import copy
from threading import Thread
from easysnmp import Session

# OID's usados ------------ HOST-RESOURCES-MIB -----------------------------------------------
HR_SWRUN_TABLE_OID = '.1.3.6.1.2.1.25.4.2.1.2' # The (conceptual) table of software running on the host.
HR_SWRUN_PERF_CPU_OID = '.1.3.6.1.2.1.25.5.1.1.1.' # The number of centi-seconds of the total system's CPU resources consumed by this process
HR_SWRUN_PERF_MEM_OID = '.1.3.6.1.2.1.25.5.1.1.2.' # The total amount of real system memory allocated to this process
HR_STORAGE_SIZE_OID = '.1.3.6.1.2.1.25.2.2.0.' # The amount of physical read-write main memory,typically RAM, contained by the host
HR_PROCESSOR_LOAD_OID = '.1.3.6.1.2.1.25.3.3.1.2' # The average, over the last minute, of the percentage of time that this processor was not idle. Implementations may approximate this one minute smoothing period if necessary
HR_SYSTEM_UPTIME_OID = '.1.3.6.1.2.1.25.1.1.0' # The amount of time since this host was last initialized

class MonitorWorker(Thread):

  def __init__(self,address, community, version, port, polling):
    Thread.__init__(self)
    self.address = address
    self.community = community
    self.version = version
    self.port = port
    self.polling = polling
    self.session = Session(hostname = address, community = community, version = version, remote_port = port)
    self.ram_size = self.get_host_ram_size()
    self.processes_ram = {}
    self.processes_cpu = {}
    print ("[+] Thread inicida para o host: " + address + ":" + port)

  # Percorre tabela de processos.
  def monitor_processes(self):
    processes = self.session.walk(HR_SWRUN_TABLE_OID) # Tabela dos processos a correr no sistema
    for process in processes: # Para cada processo retira o nome e o seu id
      process_name = process.value
      process_name = re.sub(r"/|\s+","", process_name) # Certos caracteres ñ funcionam como nome para ficheiro
      process_id = process.oid_index
      self.process_resources(process_id, process_name.lower()) # Para cada processo vai ver o seu cpu e ram

    # Estrutura em memória com %mem utilizada para cada processo
    # vai percorre-la e escrever em logs.
    for key,value in self.processes_ram.items():
      self.write_log_ram(key, value)

    # Estrutura em memória com cpu utilizado para cada processo
    # vai percorre-la e escrever em logs.
    for key,value in self.processes_cpu.items():
      self.write_log_cpu(key, value)

    # Ver percentagem de cpu utilizada em cada processador.
    self.average_cpu()

    # Reset das estrutura para o próximo polling.
    self.processes_ram.clear()
    self.processes_cpu.clear()
    
  # Para cada processo vai calcular %memória utilizada.
  def process_resources(self,processID,processName):
    date = datetime.datetime.now() # datetime da recolha
    # vai à tabela retirar total de memória alocada pelo processo
    process_ram = float(int(self.session.get(HR_SWRUN_PERF_MEM_OID + processID).value))
    # retira total de centi-seconds do cpu consumidos por este processo
    process_cpu = int(self.session.get(HR_SWRUN_PERF_CPU_OID + processID).value)

    # system uptime
    uptime = int(self.session.get(HR_SYSTEM_UPTIME_OID).value)

    # valor da percentagem memória utilizada e datetime da recolha
    value_mem = {"time": str(date), "mem": (process_ram / float(self.ram_size)) * 100}
    # valor total de centi-seconds do cpu consumidos e datetime da recolha
    value_cpu = {"time": str(uptime), "cpu": process_cpu,"date": str(date)}

    # processos com threads
    # se tem threads: update do valor
    # otherwise: escreve valor
    if processName not in self.processes_ram:
      self.write_ram_list(processName, value_mem)
      self.write_cpu_list(processName, value_cpu)
    else:
      self.update_ram_list(processName, value_mem)
      self.update_cpu_list(processName, value_cpu)

  # -------------------------------------------------------------------------------------------
  # Funções acerca do CPU
  # -------------------------------------------------------------------------------------------

  # Percentagem de cpu médio utilizado no host
  def average_cpu(self):
    processors = self.session.walk(HR_PROCESSOR_LOAD_OID)
    date = datetime.datetime.now() # Datetime da recolha
    count = 0
    for processor in processors:
      processorLoad = float(processor.value)
      number = processor.oid_index
      processor_value = {"time": str(date),"value": processorLoad ,"cpu": count}
      count += 1
      with open('logs/' + self.address +'/' + 'cpugeral', 'a+') as outfile:
        json.dump(processor_value, outfile)
        outfile.write('\n')

  # Escreve {time,cpu} para o processo correspondente numa estrutura em memória.
  def write_cpu_list(self,process_name,value):
    self.processes_cpu[process_name] = value

  # Dá update do valor {time,cpu} para um processo que tenha mais que uma thread.
  def update_cpu_list(self,process_name,value):
    old_value = self.processes_cpu[process_name]
    new_value = {"time": value["time"], "cpu": old_value["cpu"] + value["cpu"], "date": value["date"]}
    self.processes_cpu[process_name] = new_value

  # Escreve {time,cpu} para o ficheiro de logs do processo correspodente.
  def write_log_cpu(self,process_name,value):
    with jsonlines.open('logs/' + self.address + '/CPU/' + process_name + '.jsonl', mode='a') as writer:
      writer.write(value)
      writer.close()

  # -------------------------------------------------------------------------------------------
  # Funções acerca da RAM
  # -------------------------------------------------------------------------------------------

  # Escreve {time,%ram} para o processo correspondente numa estrutura em memória.
  def write_ram_list(self,process_name,value):
    self.processes_ram[process_name] = value

  # Dá update do valor {time,%ram} para um processo que tenha mais que uma thread.
  def update_ram_list(self,process_name,value):
    old_value = self.processes_ram[process_name]
    new_value = {"time": value["time"], "mem": old_value["mem"] + value["mem"]}
    self.processes_ram[process_name] = new_value

  # Escreve {time,%ram} para o ficheiro de logs do processo correspondente.
  def write_log_ram(self,process_name,value):
    with jsonlines.open('logs/' + self.address + '/RAM/' + process_name + '.jsonl', mode='a') as writer:
      writer.write(value)
      writer.close()

  # Retira tamanho da ram do host.
  def get_host_ram_size(self):
    return int(self.session.get(HR_STORAGE_SIZE_OID).value)

  # Cria pastas de logs necessárias.
  def generate_logs_folder(self):
    current_dir = os.getcwd()
    if not os.path.exists(current_dir+'/logs'):
      create_dir(current_dir+'/logs')
    if not os.path.exists(current_dir+'/logs/'+self.address):
      create_dir(current_dir+'/logs/'+self.address)
    if not os.path.exists(current_dir+'/logs/'+self.address+'/RAM'):
      create_dir(current_dir+'/logs/'+self.address+'/RAM')
    if not os.path.exists(current_dir+'/logs/'+self.address+'/CPU'):
      create_dir(current_dir+'/logs/'+self.address+'/CPU')

  # -------------------------------------------------------------------------------------------
  # Monitorização thread
  # -------------------------------------------------------------------------------------------

  # Monitorização.
  def run(self):
    self.generate_logs_folder() # Cria pastas de logs para o host
    while True:
      self.monitor_processes() # Inicia monitorização de processos
      time.sleep(self.polling) # Polling
# -------------------------------------------------------------------------------------------
# Funções auxiliares
# -------------------------------------------------------------------------------------------

# Cria diretorias
def create_dir(path):
  try:
    os.mkdir(path)
  except OSError:
    print ("Erro na criação da diretoria %s" % path)
