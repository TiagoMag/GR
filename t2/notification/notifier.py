import os
import sys
import signal
import configparser
import threading
import pyinotify
import subprocess
from threading import Thread
from notifierworker import NotifierWorker

# Trata do sinal.
exit_event = threading.Event()
def signal_handler(sig, frame):
  print('Notificador terminado ...')
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
        config.read('config.data') # Inicia parsing do ficheiro de configuração
    except configparser.Error :
        print("Erro na leitura da config")   

    for process in config.sections():
        if (process == 'GmailUser') : break
        try:
            host = config[process]['host'] # Retira host
            tipo = config[process]['type'] # Retira tipo
            if tipo != ('RAM' or 'CPU') : print("Valor de tipo no ficheiro de configuração errado (CPU ou RAM)")
            threshold = float(config[process]['threshold']) # Retira threshold 
        except configparser.NoOptionError:
            print('Erro no ficheiro de configuração falta de opção host/type/threshold')
        # Iniciar thread para notificar.
        thread = NotifierWorker(host, tipo, threshold, process)
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
