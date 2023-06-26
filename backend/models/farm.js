const mongoose = require("mongoose");

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

const Farm = mongoose.model("Farm", farmSchema);

module.exports = { Farm };
