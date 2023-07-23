const { Farm } = require("../models/farm");

exports.getFarmByCode = async (code) => {
  return await Farm.findOne({ code });
};

exports.getFarmByID = async (farmID) => {
  return await Farm.findById(farmID);
};

exports.getFarmByIDs = async (farmIDs) => {
  return await Farm.find({ _id: { $in: farmIDs } });
};

exports.createFarm = async (userID, farmName) => {
  const farm = new Farm({ owner: userID, name: farmName });
  return await farm.save();
};

exports.addWorker = async (farmID, userID) => {
  await Farm.findByIdAndUpdate(farmID, {
    $push: { employees: userID },
  });
};
