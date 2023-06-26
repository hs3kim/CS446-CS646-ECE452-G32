const { Farm } = require("../models/inventory");

exports.getFarmByCode = async (code) => {
  return await Farm.findOne({ code });
};
