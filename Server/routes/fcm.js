var express = require('express');
var router = express.Router();
var FCM = require('fcm-push');

var serverkey = 'AIzaSyBoQ8zUeNyIZ0yCMDx-5BVh55W-HwpbRO4';
var fcm = new FCM(serverkey);


var Message = function(){
  this.to = '';
  this.collapse_key = '';
  this.data = {};
  this.notification = {};
}

var message = {
    // to: 'registration_token_or_topics', // 보낼 사람의 기기 토큰을 넣는다.
    to: '',
    collapse_key: 'your_collapse_key',
    data: {                            //이안의 보낼 데이터를 세팅한다.
        data1: 'Data1 입니다.',
        text: 'Data2 입니다.'
    },
    notification: {
        // title: 'Title of your push notification',
        // body: 'Body of your push notification'
    }
};

router.get('/', function(req, res, next) {
  fcm.send(message, function(err,response){
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
  // var message = new Message();
  message.to = req.body.from;
  // message.data = {
  //   from: req.body.from,
  //   time: req.body.time,
  //   text: req.body.text
  // };

  fcm.send(message, function(err,response){
    if(err) {
      console.log(err);
      console.log("Something has gone wrong !");
    } else {
      console.log("Successfully sent with resposne :",response);
    }
  });
  res.json({userId:'testId', password:'testPassWord'});
});

module.exports = router;
