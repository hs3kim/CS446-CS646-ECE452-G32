const { Farm } = require("../models/farm");

exports.getFarmByCode = async (code) => {
  return await Farm.findOne({ code });
};
