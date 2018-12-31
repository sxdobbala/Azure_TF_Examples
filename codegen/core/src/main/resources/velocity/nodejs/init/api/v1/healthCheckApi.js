'use strict';

const healthCheckService = require('../../core/services/healthCheckService');

exports.getHealth = (req, res, next) => {
  healthCheckService.getHealth((err, results) => {
    if (err) {
      return next(err);
    }

    return res.send(results);
  })
};
