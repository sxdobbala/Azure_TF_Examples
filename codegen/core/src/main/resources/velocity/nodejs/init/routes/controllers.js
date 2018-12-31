'use strict';

const swaggerUi = require('swagger-ui-express');
const swaggerJSDoc = require('swagger-jsdoc');
const swaggerSpec = require('../config/swagger.conf');

const path = require('path');

const main = require('./main');
const healthCheck = require('./healthCheck');

module.exports = (app) => {
  // Swagger UI route
  app.use('/swagger/swagger-ui.html', swaggerUi.serve, swaggerUi.setup(swaggerJSDoc(swaggerSpec)));

  app.use('/', main);
  app.use('/health', healthCheck);

  // Error Handler
  app.use((err, req, res, next) => {
    console.log(err);

    // Check if 404
    if (err.status === 404) {
      return res.sendStatus(404);
    }

    return res.status(err.status || 500).json({
      message: err.message || 'Internal Server Error'
    })
  })
};

/** Error definition for the error handler function defined above
 * @swagger
 *  definitions:
 *    Error:
 *      type: object
 *      required:
 *        - message
 *      properties:
 *        message:
 *          type: string
 */
