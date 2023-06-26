const mongoose = require("mongoose");

const cropInventorySchema = new mongoose.Schema({
  farm: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Farm",
  },
  product: String,
  count: {
    type: Number,
    min: 0,
  },
});

cropInventorySchema.index({ farm: 1, product: 1 }, { unique: true });

const CropInventory = mongoose.model("CropInventory", cropInventorySchema);

module.exports = { CropInventory };
