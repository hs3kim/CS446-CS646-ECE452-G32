const mongoose = require("mongoose");

const { Counter } = require("./counter");

const farmSchema = new mongoose.Schema({
  code: {
    type: Number,
    unique: true,
  },
  name: String,
  owner: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "User",
  },
  employees: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
    },
  ],
});

farmSchema.pre("save", function (next) {
  const farm = this;
  Counter.findOneAndUpdate(
    { name: "farmCounter" },
    { $inc: { seq: 1 } },
    { new: true, upsert: true }
  )
    .then(function (counter) {
      farm.code = counter.seq;
      next();
    })
    .catch(function (error) {
      return next(error);
    });
});

const Farm = mongoose.model("Farm", farmSchema);

module.exports = { Farm };
