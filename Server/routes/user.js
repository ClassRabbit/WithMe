var express = require('express');
var User = require('../models/User');
var router = express.Router();
var multer = require('multer');
var fs = require('fs');

var storage = multer.diskStorage({
        destination: function(req, file, cb) {
            cb(null, './public/images/user'); // Make sure this folder exists
        },
        filename: function(req, file, cb) {
            // var ext = file.originalname.split('.').pop();
            // cb(null, file.fieldname + '-' + Date.now() + '.' + ext);
            cb(null, file.originalname);
        }
    }),
    upload = multer({ storage: storage }).single('file');

/* GET users listing. */
router.get('/', function(req, res, next) {
  res.send('respond with a resource');
});

router.post('/', function(req, res, next) {
  console.log(req.body);
  var newUser = new User({
    mail : req.body.mail,
    password : req.body.password,
    token : req.body.token,
    name: req.body.name,
    birth: req.body.birth,
    phone: req.body.phone,
    gender: req.body.gender
  });
  newUser.save(function(err, user){
    if(err) {
      console.log("사용자 등록 실패");
      console.log(err);
      return res.json({result: 'fail'});
    }
    console.log("사용자 등록 성공");
    return res.json({result: 'success'});
  });
});

router.post('/login', function(req, res, next) {
  console.log(req.body);
  User.findOne({mail: req.body.mail, password: req.body.password}, function(err, user){
    if(err){
      console.log('err');
      return res.json({result: 'error'});
    }
    if(user != null){
      console.log('user !== null');
      user.result = 'success';
      console.log(user);
      return res.json({result: 'success', user: user});
    }
    else {
      console.log('else');
      return res.json({result: 'fail'});
    }
  });
});

router.put('/', function(req, res, next) {
  res.send('respond with a resource');
  console.log(req.body);
});

module.exports = router;
