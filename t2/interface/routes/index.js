var express = require('express');
var router = express.Router();
var moment = require('moment'); 
var axios = require('axios')

var apiadd = 'http://127.0.0.1:5000/'

/* GET página com os hosts. */
router.get('/', function(req, res) {
  date = moment(new Date(Date.now() )).format('YYYY-MM-DD hh:mm:ss A')
  axios.get(apiadd+'monitoring/hosts')
  .then(dados => res.render('index', {list: dados.data,date: date}))
  .catch(err => res.render('error', {error: err}))
});

/* GET página com top 10 processos e informações extra de um host */
router.get('/:ip', function(req, res) {
  var address = req.params.ip
  axios.get( apiadd + 'monitoring/' + address +'?topBy=mem')
  .then(dados =>res.render('host', {list: dados.data.data, host_name: address}))
  .catch(err => res.render('error', {error: err}))
});

/* GET página com processos de baixa duração de um host */
router.get('/:ip/baixaduracao', function(req, res) {
  date = moment(new Date(Date.now() )).format('YYYY-MM-DD hh:mm:ss A')
  var address = req.params.ip
  axios.get(apiadd + 'monitoring/' + address + '/grupos?q=baixa')
  .then(dados => {res.render('baixaduracao', {date: date,list: dados.data.data,host:address})})
  .catch(err => res.render('error', {error: err}))  
});

/* GET página com processos de média duração de um host */
router.get('/:ip/mediaduracao', function(req, res) {
  date = moment(new Date(Date.now() )).format('YYYY-MM-DD hh:mm:ss A')
  var address = req.params.ip
  axios.get(apiadd+'monitoring/' + address + '/grupos?q=media')
  .then(dados => {res.render('mediaduracao', {date: date,list: dados.data.data,host:address})})
  .catch(err => res.render('error', {error: err}))  
});

/* GET página com processos de longa duração de um host */
router.get('/:ip/longaduracao', function(req, res) {
  date = moment(new Date(Date.now() )).format('YYYY-MM-DD hh:mm:ss A')
  var address = req.params.ip
  axios.get(apiadd+'monitoring/' + address + '/grupos?q=alta')
  .then(dados => {res.render('longaduracao', {date: date,list: dados.data.data,host:address})})
  .catch(err => res.render('error', {error: err}))  
});

/* GET página gráfico de % média cpu utilizado em todos os cpus */
router.get('/:ip/cpugeral', function(req, res) {
  var address = req.params.ip
  var name = req.params.name
  date = moment(new Date(Date.now() )).format('YYYY-MM-DD hh:mm:ss A')
  axios.get(apiadd+'monitoring/' + address + '/cpugeral')
  .then(dados => {
                    datasets = []
                    size = dados.data.size
                    var labels = []
                    var data = []
                    dados.data.data.map(element => {data.push( {"x":moment(new Date(element.time)).format('YYYY-MM-DD hh:mm:ss'),"y": parseFloat(element.value),"id":parseInt(element.id)} ) });
                    dados.data.data.map(element => {labels.push(moment(new Date(element.time)).format('YYYY-MM-DD hh:mm:ss')) } );
                    for(var i=0;i < size;i++){
                      datasets.push((data.filter(item => {return item.id == i}).sort(compare)));                                                   
                    }
                    let unique = [...new Set(labels)];
                    res.render('cpusgraph', {date: date,host: address,size: size,nome: name,listdata: JSON.stringify(datasets),listlabels: JSON.stringify(unique.sort())})
                })
  .catch(err => res.render('error', {error: err}))  
});

/* GET página com index de processo para gráficos de ram ou cpu */
router.get('/:ip/:name', function(req, res) {
  var address = req.params.ip
  var name = req.params.name
  res.render('graphs', {nome: name,host: address})
});

/* GET página de um processo com gráficos da ram */
router.get('/:ip/:name/raminfo', function(req, res) {
  date = moment(new Date(Date.now() )).format('YYYY-MM-DD hh:mm:ss A')
  var address = req.params.ip
  var name = req.params.name
  axios.get(apiadd+'monitoring/' + address + '/' + name +'?q=mem')
  .then(dados => {
                  var labels = []
                  var data = []
                  dados.data.data.map(element => {data.push( {"x":moment(new Date(element.x)).format('YYYY-MM-DD hh:mm:ss'),"y": parseFloat(element.y)} ) });
                  dados.data.data.map(element => {labels.push(moment(new Date(element.x)).format('YYYY-MM-DD hh:mm:ss')) } );
                  res.render('graphram', {host:address,date: date,nome: name,listdata: JSON.stringify(data.sort(compare)),listlabels: JSON.stringify(labels.sort())})
                })
  .catch(err => res.render('error', {error: err}))  
});

/* GET página de um processo com gráficos do cpu*/
router.get('/:ip/:name/cpuinfo', function(req, res) {
  date = moment(new Date(Date.now() )).format('YYYY-MM-DD hh:mm:ss A')
  var address = req.params.ip
  var name = req.params.name
  axios.get(apiadd+'monitoring/' + address + '/' + name +'?q=cpu')
  .then(dados => {
                  var labels = []
                  var data = []
                  dados.data.data.map(element => {data.push( {"x":element.x,"y": parseFloat(element.y)} ) });
                  dados.data.data.map(element => {labels.push(element.x) } );
                  res.render('graphcpu', {host: address,date: date,nome: name,listdata: JSON.stringify(data.sort(compare)),listlabels: JSON.stringify(labels.sort())})
                })
  .catch(err => res.render('error', {error: err}))  
});

/* Funções auxiliares */

function compare(a,b) {
  if ( Date.parse(a.x) < Date.parse(b.x) ){
    return -1;
  }
  if ( Date.parse(a.x) > Date.parse(b.x) ){
    return 1;
  }
  return 0;
}

module.exports = router;
