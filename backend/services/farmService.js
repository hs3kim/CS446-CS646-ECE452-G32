const { Farm } = require("../models/farm");

exports.getFarmByCode = async (code) => {
  return await Farm.findOne({ code });
};

exports.createFarm = async (userID, farmName) => {
  const farm = new Farm({ owner: userID, name: farmName });
  return await farm.save();
};
