var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var schema = new Schema({
  user: {type: Schema.Types.ObjectId, required: true},
  title: {type: String, required: true},
  content: {type: String, required: true},
  limit: {type: Number, required: true},
  numberOfImages: {type: Number},
  latitude: {type: Number},
  longitude: {type: Number},
  address: {type: String},
  // attender : [Schema.Types.ObjectId]
  createdAt: {type: Date, default: Date.now}
}, {
  toJSON: {virtuals: true},
  toObject: {virtuals: true}
});

var Room = mongoose.model('Room', schema);

module.exports = Room;
