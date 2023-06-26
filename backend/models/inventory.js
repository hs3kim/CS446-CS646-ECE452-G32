const mongoose = require("mongoose");

const farmSchema = new mongoose.Schema({
  code: {
    type: Number,
    unique: true,
  },
  name: String,
});

const cropInventorySchema = new mongoose.Schema({
  farm: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Farm",
  },
  product: String,
  count: Number,
});

cropInventorySchema.index({ farm: 1, product: 1 }, { unique: true });

const Farm = mongoose.model("Farm", farmSchema);
const CropInventory = mongoose.model("CropInventory", cropInventorySchema);

module.exports = { Farm, CropInventory };
