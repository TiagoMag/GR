# Trabalho Prático 3
## Elementos

Trabalho realizado pelos elementos:

- [André Morais, A83899](https://github.com/Demorales1998)
- [Tiago Magalhães, A84485](https://github.com/TiagoMag)

## Sobre 
Agente SNMP para monitorização de datas de eventos.

## Requisitos

**Java** 11+  </br>
**SNMP** </br>
## Instalação

- Clone/download repositório.

## Uso
- Ir para diretoria do executável
```
cd agenteEvento
```
- Ligar agente:</br>
``` 
java -jar Agente.jar
``` 
- Testar agente (exemplo):</br>
```
snmpwalk -v2c -c public localhost:3003 1.3.6.1.2.1.60.10
snmpwalk -v2c -c public localhost:3003 1.3.6.1.2.1.60.10.4
```
