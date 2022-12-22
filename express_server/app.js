const express = require("express");

const port = process.env.PORT || 3000;
const EXPECTED_REQUEST_BODY_LENGTH = 1024;

const app = express();
app.use(express.json());
app.use(express.text());

app.post("/healthCheck", async (request, response) => {
  console.log(
    `Received health check request. Content length ${request.body.length}`
  );

  response
    .status(200)
    .json({ healthy: request.body.length == EXPECTED_REQUEST_BODY_LENGTH });
});

app.post("/echo", (request, response) => {
  console.log(`Received /POST request on path ${request.url}`);
  console.log(`Body: ${JSON.stringify(request.body)}`);

  response.status(200).json({ msg: request.body });
});

app.listen(port, () => {
  console.log(`Server started on port ${port}`);
});
