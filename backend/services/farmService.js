const { Farm } = require("../models/farm");
const { User } = require("../models/user");

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

exports.editFarm = async (userID, code, name) => {
  const updatedFarm = await Farm.findOneAndUpdate(
    { owner: userID, code: code },
    { $set: { name } }, // Update the name and owner fields
    {new: true} 
  );
  if (!updatedFarm) {
    throw new AppError("Farm was not updated");
  }
  return updatedFarm;
}

exports.deleteFarm = async (userID, farmCode) => {
  const deletedFarm = Farm.findOneAndDelete({ owner: userID, code: farmCode });
  if (!deletedFarm) {
    throw new AppError("Farm was not deleted");
  }
  await User.updateMany({ worksAt: deletedFarm._id }, { $pull: { worksAt: deletedFarm._id } });
  await User.updateOne({ owns: deletedFarm._id }, { $pull: { owns: deletedFarm._id } });
  return deletedFarm;
};

exports.addWorker = async (farmID, userID) => {
  await Farm.findByIdAndUpdate(farmID, {
    $push: { employees: userID },
  });
};
