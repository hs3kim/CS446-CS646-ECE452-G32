const { User } = require("../models/user");

exports.addOwnedFarm = async (farmID, userID) => {
  await User.findByIdAndUpdate(userID, {
    $push: { owns: farmID },
  });
};

exports.addWorkedFarm = async (farmID, userID) => {
  await User.findByIdAndUpdate(userID, {
    $push: { worksAt: farmID },
  });
};

exports.getUserByID = async (userID) => {
  return await User.findById(userID);
};

exports.updateUserProfile = async (userId, newProfile) => {
  if (newProfile.hasOwnProperty('email')) {
      newProfile.email = newProfile.email.toLowerCase()
  }

  const updatedUser = await User.findByIdAndUpdate(userId, newProfile, {new: true});
  if (!updatedUser) {
      throw new AppError("BAD_REQUEST", "User Does Not Exist");
  }
  return updatedUser
};
