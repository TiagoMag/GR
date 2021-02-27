import os
import jsonlines
import json
import pyinotify
from sendmail import send
from threading import Thread

class NotifierWorker(Thread):

  def __init__(self,host, tipo, threshold, name):
    Thread.__init__(self)
    self.host = host
    self.tipo = tipo
    self.threshold = threshold
    self.name = name
    self.path = '../monitor/logs/' + self.host +'/' + self.tipo + '/' + self.name + '.jsonl'

  # Verifica mudanças no ficheiro de logs. 
  def onChange(self,ev):
    key = ""
    with jsonlines.open(self.path) as reader:
        for obj in reader:
            pass    
        if self.tipo == 'RAM':
            key = 'mem'
        elif self.tipo == "CPU":
            key = 'cpu'    
        threshold = obj[key]
    if threshold >= self.threshold:
        send(self.name, self.threshold, self.tipo, self.host)

  def run(self):
    wm = pyinotify.WatchManager()
    wm.add_watch(self.path, pyinotify.IN_MODIFY, self.onChange)
    notifier = pyinotify.Notifier(wm)
    notifier.loop()

           
