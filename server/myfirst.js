const mongo = require('mongodb');
const url = "mongodb+srv://server:madcamp@cluster0-cgg1u.mongodb.net/test?retryWrites=true&w=majority";


mongo.connect(url, {useNewUrlParser: true}, (err, db) => {
    if(err) {
       console.log(err);
       process.exit(0);
    }
    console.log('Connected...');
    var dbo = db.db('Madcamp_Second');
    dbo.createCollection('products', (err, result) => {
        if(err) {
           console.log(err);
           process.exit(0);
        }
        console.log('collection created!');
        mydb = db;
    });
  });

var dbo = mydb.db('Madcamp_Second');
var myobj = {id: "1", name: "americano"};
dbo.collection("products").insertOne(myobj, function(err, res){
    if (err) throw err;
    console.log("1 document inserted");
})



mongo.connect(url, {useNewUrlParser: true}, (err, db) => {
        if(err) {
           console.log(err);
           process.exit(0);
        }
        console.log('Connected...');
        var dbo = db.db('sample_airbnb');
        var myobj = { id: "1", name: "americano" };
        dbo.collection("products").insertOne(myobj, function(err, res) {
            if (err) throw err;
            console.log("1 document inserted");
            db.close();
        });
});