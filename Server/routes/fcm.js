var express = require('express');
var router = express.Router();
var FCM = require('fcm-push');

var serverkey = 'AIzaSyBoQ8zUeNyIZ0yCMDx-5BVh55W-HwpbRO4';
var fcm = new FCM(serverkey);

var message = {
    // to: 'registration_token_or_topics', // 보낼 사람의 기기 토큰을 넣는다.
    to: 'fcBQTnV3Q8o:APA91bEMktSEVXFINk8y24FKnqS0mpykHuVK1PbQRU1pO91pGA73TwJc6g8RHUnTRMJvOEy5c39xizbhdFULOqQsw6A0huxvrY1go4eRZ8zn1ugI-3xc5t4EDltV6vTdaMtyz8uLL9i4',
    collapse_key: 'your_collapse_key',
    data: {                            //이안의 보낼 데이터를 세팅한다.
        data1: 'Data1 입니다.',
        data2: 'Data2 입니다.'
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

module.exports = router;
