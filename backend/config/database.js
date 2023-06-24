const mongoose = require("mongoose");

exports.connectDB = async () => {
  try {
    mongoose.set("strictQuery", false);
    await mongoose.connect(process.env.MONGO_DB_URL, {});
    console.log("DB SUCCESSFULLY CONNECTED");
  } catch (err) {
    console.log("DB CONNECTION FAILED...");
    console.error(err);
    process.exit(1);
  }
};
