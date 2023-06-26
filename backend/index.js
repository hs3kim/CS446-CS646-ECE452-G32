const express = require("express");
const cookieParser = require("cookie-parser");
const cors = require("cors");
require("dotenv").config();
require("express-async-errors");

const { connectDB } = require("./config/database");
const { globalErrorHandler } = require("./middlewares/errorHandling");
// const { verifyToken } = require("./middlewares/auth");

const authRoutes = require("./routes/authRoutes");
const inventoryRoutes = require("./routes/inventoryRoutes");

const port = process.env.PORT || 8080;

const app = express();

app.use(express.json());
app.use(cookieParser());
app.use(cors());

app.get("/", (_, res) => {
  res.send("FarmWise Backend Server");
});

app.use("/api/auth", authRoutes);
app.use("/api/inventory", inventoryRoutes);

app.use(globalErrorHandler);

const startApp = async () => {
  await connectDB();
  app.listen(port, () => {
    console.log(`APP RUNNING ON PORT ${port}`);
  });
};

startApp();
