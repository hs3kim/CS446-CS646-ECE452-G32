const { CropInventory } = require("../models/inventory");
const { Farm } = require("../models/farm");
const { AppError } = require("../utils/errors");

exports.addCropInventory = async (farmCode, productInfo) => {
  const farm = await Farm.findOne({ code: farmCode });
  const product = productInfo.item;
  const count = productInfo.quantity;
  if (!farm) {
    throw new AppError("BAD_REQUEST", "Farm Does Not Exist");
  }
  let invenEntry = await CropInventory.findOne({
    farm: farm._id,
    product: product,
  });
  if (invenEntry) {
    invenEntry.count += count;
    try {
      invenEntry.save();
    } catch (error) {
      throw new AppError("THIRD_PARTY_ERROR", "Inventory has not been updated");
    }
    return invenEntry;
  } else {
    invenEntry = await CropInventory.create({
      farm: farm._id,
      product: product,
      count: count,
    });
    return invenEntry;
  }
};

exports.getInventory = async (farmID) => {
  return await CropInventory.find({ farm: farmID });
};
