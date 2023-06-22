const express = require("express");
require("dotenv").config();
require("express-async-errors");

const { connectDB } = require("./config/database");
const { globalErrorHandler } = require("./middlewares/errorHandling");

const authRoutes = require("./routes/authRoutes");

const port = process.env.PORT || 8080;

const app = express();

app.use(express.json());

app.get("/", (_, res) => {
  res.send("FarmWise Backend Server");
});

app.use("/api/auth", authRoutes);

app.use(globalErrorHandler);

const startApp = async () => {
  await connectDB();
  app.listen(port, () => {
    console.log(`APP RUNNING ON PORT ${port}`);
  });
};

startApp();
