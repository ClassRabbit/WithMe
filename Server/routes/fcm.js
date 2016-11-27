var express = require('express');
var router = express.Router();
var Join = require('../models/Join');
var Room = require('../models/Room');
var User = require('../models/User');
var Chat = require('../models/Chat');
var FCM = require('fcm-node');

var serverkey = 'AIzaSyBoQ8zUeNyIZ0yCMDx-5BVh55W-HwpbRO4';
var fcm = new FCM(serverkey);


var Message = function(){
  // this.to = '';
  this.registration_ids = [];
  // this.collapse_key = '';
  this.data = {};
  this.property = 'high';
  this.content_available = true;
  this.notification = {};
};

var messageSample = {
    // to: 'registration_token_or_topics', // 보낼 사람의 기기 토큰을 넣는다.
    to: 'fJ2hwwfY7D8:APA91bHUh4QtOfA9u2WPRnwJnE0p0X0Zhly4t1kegrfOOGIlasH5OgbBtPuj2Kc7ykdfW0p0DgNgyDWYI3NHyRRHOKs08zZuKbJE2lBCxhtO_omhd4S9oI9G0h7V43nuDIn1396LwayB',
    collapse_key: 'your_collapse_key',
    data: {                            //이안의 보낼 데이터를 세팅한다.
        data1: 'Data1 입니다.',
        data2: 'testTime',
        data3: 'Data2 입니다.'
    },
    notification: {
        title: 'Title of your push notification',
        body: 'Body of your push notification'
    }
};

router.get('/', function(req, res, next) {
  fcm.send(messageSample, function(err,response){
      if(err) {
          console.log("Something has gone wrong !");
      } else {
          console.log("Successfully sent with resposne :",response);
      }
  });
  res.json({userId:'testId', password:'testPassWord'});
});

router.post('/', function (req, res){
	console.log(req.body.test);
  message.to = req.body.token;

  fcm.send(message, function(err,response){
      if(err) {
          console.log("Something has gone wrong !");
      } else {
          console.log("Successfully sent with resposne :",response);
      }
  });
  res.json({userId:'testId', password:'testPassWord'});
});

router.post('/chat', function (req, res){
	console.log(req.body);
  Join.findOne({user: req.body.user}, function(err, join){
    if(err) {
      console.log(err);
      return;
    }
    Join.find({room: join.room}, function(err, joins){
      if(err){
        console.log(err);
        return;
      }
      var query = [];
      for(var i in joins){
        query.push(joins[i].user);
      }
      User.find({_id:{ $in: query }}, function(err, users){
        if(err){
          console.log(err);
          return;
        }
        var registration_ids = [];
        for(var j in users){
          if(users[j].token !== ""){
            registration_ids.push(users[j].token);
          }
        }
        console.log('length is ' + registration_ids.length);
        var message = new Message();
        // message.to = to;
        message.registration_ids = registration_ids;
        message.data = {
          data1: req.body.user,
          data2: req.body.name,
          data3: req.body.time,
          data4: req.body.text
        };
        fcm.send(message, function(err,response){
          if(err) {
            console.log(err);
            console.log("Something has gone wrong !");
          } else {
            console.log("Successfully sent with resposne :",response);
          }
        });
      });
    });
  });
  // var message = new Message();
  // message.to = req.body.from;
  // message.data = {
  //   data1: req.body.from,
  //   data2: req.body.time,
  //   data3: req.body.text
  // };

  // fcm.send(message, function(err,response){
  //   if(err) {
  //     console.log(err);
  //     console.log("Something has gone wrong !");
  //   } else {
  //     console.log("Successfully sent with resposne :",response);
  //   }
  // });
  res.json({userId:'testId', password:'testPassWord'});
});

module.exports = router;
