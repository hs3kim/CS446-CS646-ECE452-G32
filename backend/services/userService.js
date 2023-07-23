const { User } = require("../models/user");

exports.addOwnedFarm = async (farmID, userID) => {
  await User.findByIdAndUpdate(userID, {
    $push: { owns: farmID },
  });
};

exports.getUserByID = async (userID) => {
  return await User.findById(userID);
};
