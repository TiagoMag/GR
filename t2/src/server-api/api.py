import os
import flask
import time
import configparser
import operator
import json
import jsonlines
from flask import jsonify
from flask import Flask, request
from sortedcontainers import SortedDict
from flask_cors import CORS, cross_origin
from datetime import date,datetime, timedelta

app_dir = "../monitor/"
logs_dir = "../monitor/logs/"

app = flask.Flask(__name__)
app.config['JSON_SORT_KEYS'] = False
app.config["DEBUG"] = True
cors = CORS(app)
app.config['CORS_HEADERS'] = 'Content-Type'

# Lista de hosts monitorizados.
@app.route('/monitoring/hosts', methods=['GET'])
def host():
    config = configparser.ConfigParser()
    config.read(app_dir + 'config.ini')
    hosts_number = int(config['Hosts']['number'])
    hosts_list = []
    for i in range(hosts_number) :
        host_address = config['Hosts']['host.'+ str(i)]
        hosts_list.append(host_address)
    return '{ "hosts":' + json.dumps(hosts_list) + '}',200

# Top 10 processos que utilizaram mais %ram num dado host.
@app.route('/monitoring/<address>', methods=['GET'])
def index(address):
    str_json = host()[0]
    data = json.loads(str_json)
    if address not in data['hosts']:
       return 'Nenhum host com este endereço',404
    
    dic = {}
    typeq = request.args.get('topBy')

    if typeq == "mem":
        for filename in os.listdir(logs_dir + address + '/RAM/'):
            sample_size = 0
            med_ram = 0.0
            # Calcula % média de ram consumida pelo processo
            with jsonlines.open(logs_dir + address + '/RAM/'+ filename) as reader:
                for obj in reader:
                    med_ram += obj["mem"]
                    sample_size += 1 
            reader.close()         
        
            if sample_size != 0:
                dic[filename[:-6]] = (med_ram / sample_size) # -3 retira extensao
            else :
                dic[filename[:-6]] = 0

        sorted_d = dict(sorted(dic.items(), key=operator.itemgetter(1),reverse=True))
        top_10 = {}
        for x in list(sorted_d)[0:10]:
            top_10[x] = sorted_d[x]

        array_json = []

        for x in top_10:
            json_obj={"nome":str(x),"mem":str(top_10[x])}
            array_json.append(json_obj)
        
        json_response={"data":array_json} 
        return json.dumps(json_response),200
    elif typeq == "cpu":
        for filename in os.listdir(logs_dir + address + '/CPU/'):
            cpu = 0.0
            with jsonlines.open(logs_dir + address + '/CPU/'+ filename) as reader:
                for obj in reader:
                    pass    
                cpu = obj["cpu"]
            reader.close()           
            dic[filename[:-6]] = (cpu) # -3 retira extensao
        
    sorted_d = dict(sorted(dic.items(), key=operator.itemgetter(1),reverse=True))
    top_10 = {}
    for x in list(sorted_d)[0:10]:
        top_10[x] = sorted_d[x]

    array_json = []

    for x in top_10:
        json_obj={"nome":str(x),"cpu":str(top_10[x])}
        array_json.append(json_obj)
    
    json_response={"data":array_json} 
    return json.dumps(json_response),200


# % Historico ram e cpu utilizado por um processo query 
# string _sort = lasthour,lastday e historico.
@app.route('/monitoring/<address>/<processo>', methods=['GET'])
def ram(address,processo):
    str_json = host()[0]
    data = json.loads(str_json)
    if address not in data['hosts']:
       return 'Nenhum host com este endereço',404

    today = datetime.today()
    day_before = today - timedelta(days=1)

    array=[]
    jsonobj={}

    typeq = request.args.get('q')
    if typeq == "mem":
        sort_type = request.args.get('_sort')
        # tipos de ordenamento de data 
        if(sort_type == "lastday"):
            with jsonlines.open(logs_dir + address + '/RAM/'+ processo + '.jsonl') as reader:
                for obj in reader:
                    key_time = datetime.strptime(obj["time"], '%Y-%m-%d %H:%M:%S.%f')
                    if  day_before <= key_time <= today:  
                        jsonobj={"x":obj["time"],"y":obj["mem"]}
                        array.append(jsonobj)
                reader.close()         
        elif (sort_type == "lasthour"):
            last_hour_date_time = datetime.now() - timedelta(hours = 1)
            with jsonlines.open(logs_dir + address + '/RAM/'+ processo + '.jsonl') as reader:
                for obj in reader:
                    key_time = datetime.strptime(obj["time"], '%Y-%m-%d %H:%M:%S.%f')
                    if  last_hour_date_time <= key_time <= today:  
                        jsonobj={"x":obj["time"],"y":obj["mem"]}
                        array.append(jsonobj)
                reader.close()    
        else:  
            with jsonlines.open(logs_dir + address + '/RAM/'+ processo + '.jsonl') as reader:
                for obj in reader:
                    jsonobj={"x":obj["time"],"y":obj["mem"]}
                    array.append(jsonobj)
                reader.close() 
    elif typeq == "cpu" : 
        sort_type = request.args.get('_sort')
        if(sort_type == "lastday"):
            with jsonlines.open(logs_dir + address + '/CPU/'+ processo + '.jsonl') as reader:
                x = 0
                for obj in reader:
                    key_time = datetime.strptime(obj["date"], '%Y-%m-%d %H:%M:%S.%f')
                    if x == 0 and day_before <= key_time <= today: 
                        cputime_1 = float(obj["cpu"]) * 0.01 
                        time_1 = int(obj["time"]) # datetime.strptime(obj["time"], '%Y-%m-%d %H:%M:%S.%f')
                        x+=2
                    elif x==2 and day_before <= key_time <= today : 
                        cputime_2 = float(obj["cpu"]) * 0.01
                        time_2 = int(obj["time"]) # datetime.strptime(obj["time"], '%Y-%m-%d %H:%M:%S.%f')
                        delta_cpu = cputime_2 - cputime_1
                        delta_time = (time_2 - time_1) #.total_seconds()
                        cpu_usage = (delta_cpu / delta_time) * 100
                        seconds = time_1/100
                        up = timedelta(seconds=seconds)
                        jsonobj={"x": str(up),"y": cpu_usage}
                        array.append(jsonobj)
                        x = 1
                    elif x==1 and day_before <= key_time <= today:  
                        cputime_1 = cputime_2
                        time_1 = time_2
                        x = 2   
                reader.close()
        elif(sort_type == "lasthour"):
            last_hour_date_time = datetime.now() - timedelta(hours = 1)
            with jsonlines.open(logs_dir + address + '/CPU/'+ processo + '.jsonl') as reader:
                x = 0
                for obj in reader:
                    key_time = datetime.strptime(obj["date"], '%Y-%m-%d %H:%M:%S.%f')
                    if x == 0 and last_hour_date_time <= key_time <= today: 
                        cputime_1 = float(obj["cpu"]) * 0.01 
                        time_1 = int(obj["time"]) # datetime.strptime(obj["time"], '%Y-%m-%d %H:%M:%S.%f')
                        x+=2
                    elif x==2 and last_hour_date_time <= key_time <= today : 
                        cputime_2 = float(obj["cpu"]) * 0.01
                        time_2 = int(obj["time"]) # datetime.strptime(obj["time"], '%Y-%m-%d %H:%M:%S.%f')
                        delta_cpu = cputime_2 - cputime_1
                        delta_time = (time_2 - time_1) #.total_seconds()
                        cpu_usage = (delta_cpu / delta_time) * 100
                        seconds = time_1/100
                        up = timedelta(seconds=seconds)
                        jsonobj={"x": str(up),"y": cpu_usage}
                        array.append(jsonobj)
                        x = 1
                    elif x==1 and last_hour_date_time <= key_time <= today:  
                        cputime_1 = cputime_2
                        time_1 = time_2
                        x = 2   
                reader.close()        
        else:
            with jsonlines.open(logs_dir + address + '/CPU/'+ processo + '.jsonl') as reader:
                x = 0
                for obj in reader:
                    if x == 0 : 
                        cputime_1 = float(obj["cpu"]) * 0.01 
                        time_1 = int(obj["time"]) # datetime.strptime(obj["time"], '%Y-%m-%d %H:%M:%S.%f')
                        x+=2
                    elif x==2 : 
                        cputime_2 = float(obj["cpu"]) * 0.01
                        time_2 = int(obj["time"]) # datetime.strptime(obj["time"], '%Y-%m-%d %H:%M:%S.%f')
                        delta_cpu = cputime_2 - cputime_1
                        delta_time = (time_2 - time_1) #.total_seconds()
                        cpu_usage = (delta_cpu / delta_time) * 100
                        seconds = time_1/100
                        up = timedelta(seconds=seconds)
                        jsonobj={"x": str(up),"y": cpu_usage}
                        array.append(jsonobj)
                        cputime_1 = cputime_2
                        time_1 = time_2
                reader.close() 

    objtotal = {"data": array}

    return json.dumps(objtotal),200

# %CPU consumido pelos varios cpus dos host.
@app.route('/monitoring/<address>/cpugeral', methods=['GET'])
def cpugeral(address):
    str_json = host()[0]
    data = json.loads(str_json)
    if address not in data['hosts']:
       return 'Nenhum host com este endereço',404
    
    array_json = []
    dic={}
    count=0

    with jsonlines.open(logs_dir + address + '/cpugeral') as reader:
        for obj in reader:  
            if int(obj["cpu"]) not in dic:
                dic[int(obj["cpu"])]=count
                count+=1
            array_json.append({"id":obj["cpu"],"value":obj["value"],"time":obj["time"]})
    reader.close()         
    
    json_response={"size":count,"data":array_json} 
    return json.dumps(json_response),200

# Grupos de processos.
@app.route('/monitoring/<address>/grupos', methods=['GET'])
def grupos(address):
    str_json = host()[0]
    data = json.loads(str_json)
    if address not in data['hosts']:
       return 'Nenhum host com este endereço',404
    
    longa_duracao = []
    baixa_duracao = []
    media_duracao = []

    for filename in os.listdir(logs_dir + address + '/RAM/'):
        sample_size = 0
        file = filename
        freq_med = 0.0
        freq = 0.0
        total_time = 0                                                            
        with jsonlines.open(logs_dir + address + '/RAM/'+ file ) as reader:   
            processo=file[:-6]
            x = 0
            for obj in reader:
                if x == 0 : 
                    time_1 = datetime.strptime(obj["time"], '%Y-%m-%d %H:%M:%S.%f')
                    x+=2
                elif x==2 : 
                    time_2 = datetime.strptime(obj["time"], '%Y-%m-%d %H:%M:%S.%f')
                    delta_time = (time_2 - time_1).total_seconds()
                    if(delta_time <= 40):
                        total_time += delta_time
                    else:
                        freq += delta_time
                        sample_size+=1
                    time_1 = time_2 
            reader.close() 
        if(sample_size == 0):
            freq_med == 0.0
        else:      
            freq_med = freq / sample_size
        if(total_time > 60):
            media_duracao.append({"nome":processo,"duracao":total_time,"freqmedia":freq_med})
        elif(total_time > 3600):
            longa_duracao.append({"nome":processo,"duracao":total_time,"freqmedia":freq_med})
        else:
            baixa_duracao.append({"nome":processo,"duracao":total_time,"freqmedia":freq_med})

    typeq = request.args.get('q')
    if typeq == "baixa":
        json_response={"data":baixa_duracao} 
    elif typeq == "alta":
        json_response={"data":longa_duracao} 
    elif typeq == "media":
        json_response={"data":media_duracao} 

    return json.dumps(json_response),200

if __name__ == '__main__':
    app.run()

