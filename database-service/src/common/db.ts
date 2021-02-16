import * as appConfig from "./db-config";
import * as mysql from "mysql";

export let db : mysql.Connection;

function connectDatabase() {
    if (!db) {
        db = mysql.createConnection(appConfig.dbOptions);

        db.connect(function(err){
            if(!err) {                
                console.log('Database is connected!');
            } else {
                console.log('Error connecting database!');
            }
        });
    }
    return db;
}

module.exports = connectDatabase();