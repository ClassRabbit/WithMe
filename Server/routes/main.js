var express = require('express');
var Join = require('../models/Join');
var Room = require('../models/Room');
var User = require('../models/User');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.post('/', function (req, res){
  console.log(req.body);
  Join.findOne({user: req.body.user}, function(err, join){
    if(err){
      return res.json({result: 'fail'});
    }
    Room.find({}, function(err, rooms){
      if(err){
        return res.json({result: 'fail'});
      }
      if(join === null){
        return res.json({result: 'success', isJoin: false, rooms: rooms});
      }
      else {
        Room.findById(join.room, function(err, myRoom){
          if(err){
            return res.json({result: 'fail'});
          }
          return res.json({result: 'success', isJoin: true, rooms: rooms, myRoom:myRoom});
        });
      }
    });
  });
});



module.exports = router;
