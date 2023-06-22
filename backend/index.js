const express = require("express");
require("dotenv").config();

const { connectDB } = require("./config/database");

const port = process.env.PORT || 8080;

const app = express();

app.get("/", (_, res) => {
  res.send("FarmWise Backend Server");
});

const startApp = async () => {
  await connectDB();
  app.listen(port, () => {
    console.log(`APP RUNNING ON PORT ${port}`);
  });
};

startApp();
