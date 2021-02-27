# Trabalho Prático 2
## Elementos

Trabalho realizado por:
- [Tiago Magalhães, A84485](https://github.com/TiagoMag)

## Sobre 
Ferramenta de monitorização.

## Requisitos

**NodeJs**  </br>
**npm** - Instala dependências para NodeJS</br>
**Python3**  </br>
**pip** - Instala dependências para Python </br>
**SNMP** </br>
## Instalação

- Clone/download repositório.
```
cd src/
```
- Instalar dependências (python)
```
pip install -r monitor/requirements.txt ; pip install -r notifier/requirements.txt ;  pip install -r server-api/requirements.txt 
```
- Instalar dependências (nodejs)
```
cd interface
```
```
npm i
```

## Uso
- Módulo de monitorização
```
cd monitor
python3 monitor.py
```
- Módulo de alarmes
```
cd notifier
python3 notifier.py
```
- API Server
```
cd server-api
python3 api.py
```
- Interface web
```
npm start
```
