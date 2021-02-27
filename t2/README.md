# Trabalho Prático 2
## Elementos

Trabalho realizado por:
- [Tiago Magalhães, A84485](https://github.com/TiagoMag)

## Sobre 
Ferramenta de monitorização.

## Requisitos

**NodeJs**  </br>
**npm** - Installs package dependencies for NodeJS</br>
**Python3**  </br>
**pip** Installs package dependencies for Python </br>
**SNMP** </br>
## Instalação

- Clone/download repositório.
```
cd src/
```
- Instalar dependências (python)
```
cd monitor / cd notifier / cd server.api
```
```
pip install -r requirements.txt
```
- Instalar dependências (nodejs)
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
