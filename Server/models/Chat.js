var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var schema = new Schema({
  user: {type: Schema.Types.ObjectId, required: true},
  name: {type: String, required: true},
  room: {type: Schema.Types.ObjectId, required: true},
  text: {type: String, required: true},
  chatAt: {type: String, required: true}
}, {
  toJSON: {virtuals: true},
  toObject: {virtuals: true}
});

var Chat = mongoose.model('Chat', schema);

module.exports = Chat;
