const express = require('express');
const chalk = require('chalk');

const app = express();
app.set('port',3000);

app.get("/data", function (req, res) {
    var data = ({ 
        "code":200,
        "status":"OK",
        "timestamp":new Date(),
        "message":"feed",
        "data":{ 
           "timestamp":new Date().getTime(),
           "exchange":"NSE_INDEX",
           "symbol":"NIFTY_100",
           "ltp":getRandomArbitrary(2000,9000),
           "open":getRandomArbitrary(2000,9000),
           "high":getRandomArbitrary(2000,9000),
           "low":getRandomArbitrary(2000,9000),
           "close":getRandomArbitrary(2000,9000),
           "yearly_high":9105.4,
           "yearly_low":7340.95
        }
     });
    res.status(200).send(data);
  });

  function getRandomArbitrary(min, max) {
    return Math.random() * (max - min) + min;
}


  app.listen(app.get('port'), () => {
    console.log('%s fake upstox server is running at http://localhost:%d in %s mode', chalk.green('✓'), app.get('port'));
    
    console.log('  Press CTRL-C to stop\n');
  });