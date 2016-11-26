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
      return res.json({result: 'error'});
    }
    Room.find({}, function(err, rooms){
      if(err){
        return res.json({result: 'error'});
      }
      if(join === null){
        return res.json({isJoin: false, rooms: rooms});
      }
      else {
        return res.json({isJoin: true, rooms: rooms});
      }
    });
  });
});

router.post('/view', function (req, res){
  console.log(req.body);
  Join.findOne({user: req.body.user}, function(err, join){
    if(err){
      return res.json({result: 'error'});
    }
    Room.findById(req.body.roomId, function(err, room){
      if(err){
        return res.json({result: 'error'});
      }
      if(room === null){
        return res.json({result: 'null'});
      }
      User.findById(room.user, function(err, user){
        if(err){
          return res.json({result: 'error'});
        }
        if(user === null) {
          //user가 없을때 지만 이런일이 안생기게 막음
          return;
        }
        Join.find({room: room.id}, function(err, joins){
          if(err){
            return res.json({result: 'error'});
          }
          if(joins === null) {
            //joins가 없을때 지만 이런일이 안생기게 막음
            return;
          }
          var data = [];
          data.push(room);
          data.push(user);
          data.push(joins.length);
          if(join === null){
            return res.json({isJoin: false, data: data});
          }
          else {
            return res.json({isJoin: true, data: data});
          }
        });
      });
    });
  });
});

module.exports = router;
