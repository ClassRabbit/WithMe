var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var routes = require('./routes/index');
var users = require('./routes/users');
var fcm = require('./routes/fcm');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', routes);
app.use('/users', users);
app.use('/fcm', fcm);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});



// var FCM = require('fcm').FCM;
// var apiKey = '프로젝트가 같다면 기존 GCM API 코드 쓰면 됨';
// var fcm = new FCM(apiKey);
// var message = {
//   registration_id: '단말기 토큰값', // required
//   collapse_key: 'Collapse key',
//   data1: 'this is data1 war !',
//   data2: 'this is data2 war !'
// };
//
// fcm.send(message, function(err, messageId) {
//   if (err) {
//     console.log("Something has gone wrong!");
//   }
//   else {
//     console.log("Sent with message ID: ", messageId);
//   }
// });


module.exports = app;
