const { Farm } = require("../models/farm");

exports.getFarmByCode = async (code) => {
  return await Farm.findOne({ code });
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
  return deletedFarm;
};