var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var schema = new Schema({
  user: {type: Schema.Types.ObjectId},
  title: {type: String, required: true},
  content: {type: String, required: true},
  limit: {type: Number, required: true},
  images: [String],
  latitude: {type: Number},
  longitude: {type: Number},
  attender : [Schema.Types.ObjectId]
}, {
  toJSON: {virtuals: true},
  toObject: {virtuals: true}
});

var Room = mongoose.model('Room', schema);

module.exports = Room;
