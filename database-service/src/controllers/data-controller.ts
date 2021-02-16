import { Request, Response } from "express";

var db = require('../common/db');
 
export let getAllData = async (req: Request, res: Response) => {  
    console.log("Received GetAllData ==> GET");
     
    db.query(`SELECT * FROM Data`, (error: { message: any; }, results: any, fields: any) => {
        if (error) {
          return console.error(error.message);
        }
        else{
          res.send(results);
        }        
        console.log(results);
      });
};
 
export let insertData = async (req: Request, res: Response) => {
    console.log("Received SaveData ==> POST");    
     
    let userId:String = req.body.userId;
    let timestamp:Date = req.body.timestamp;
    let latitude:number = req.body.latitude;
    let longitude:number = req.body.longitude;
      
    let query = `INSERT INTO mydatabase.Data (userId, timestamp, location) VALUES ('${userId}','${timestamp}', ST_GeomFromText('POINT(${latitude} ${longitude})'))`;
    console.error(query);
    db.query(query, (error: { message: any; }, results: any, fields: any) => {
      if (error) {        
        res.sendStatus(403);
        return console.error("ERROR: " + error.message);
      }
      else{
        res.json('Data added successfully');
      }
    });
};
