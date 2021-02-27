import os
import sys
import signal
import configparser
import threading
import pyinotify
import subprocess
from threading import Thread
from monitorworker import MonitorWorker

# Trata do sinal.
exit_event = threading.Event()
def signal_handler(sig, frame):
  print('Monitorização terminada ...')
  exit_event.set()
  os._exit(0)
signal.signal(signal.SIGINT, signal_handler)

# Verifica mudanças no ficheiro config.
def onChange(ev):
    print("Restarting monitoring...")
    restart_program()
    pass

def restart_program():
    python = sys.executable
    os.execl(python, python, * sys.argv)

def main():
    ConfigChange() # Monitoriza ficheiro config
    try :
        config = configparser.ConfigParser() # Parser para o ficheiro de configuração
        config.read('config.ini') # Inicia parsing do ficheiro de configuração
    except configparser.Error :
        print("Erro na leitura da config")    
    try :
        hosts_number = int(config['Hosts']['number']) # Retira número de hosts
    except configparser.Error :
        print("Erro no número de hosts") 

    # Percorre hosts que irão ser monitorizados.
    for i in range(hosts_number) :
        try:
            host_address = config['Hosts']['host.' + str(i)] # Endereço do host
            host_community = config['Community']['host.' + str(i)] # Comunnity string
            host_port = config['Port']['host.' + str(i)] # Porta do host
            host_polling = int(config['Polling']['host.' + str(i)]) # Polling definido
        except configparser.Error as err :
            print("Config error: {0}".format(err))
        
        # Iniciar thread de monitorização para o host.
        thread = MonitorWorker(host_address,host_community,2,host_port,host_polling)
        thread.start()
        
class ConfigChange(Thread):
    def __init__(self):
        Thread.__init__(self)
        self.daemon = True
        self.start()

    def run(self):
        # Verifica mudanças no ficheiro config.
        wm = pyinotify.WatchManager()
        wm.add_watch('config.ini', pyinotify.IN_MODIFY, onChange)
        notifier = pyinotify.Notifier(wm)
        notifier.loop()
    
if __name__ == "__main__":
    main()
