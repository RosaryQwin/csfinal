// package com.rosary;


// import java.math.BigInteger;

// import com.rosary.Task2.Consensus;
// import com.rosary.Task2.KeyLoader;
// import com.rosary.Task2.RSA;
// import com.rosary.Task3.InvenLoader;
// import com.rosary.Task3.PKG;
// import com.rosary.Task3.Task3_KeyLoader;

// public class Main {
//     public static void main(String[] args) throws Exception {
//         String item_id = "4";
//         String item_units = "12";
//         String item_price = "23";
//         String inventory_id = "A";
//         // RSA.save_record_json(item_id,item_units,item_price,inventory_id);
//         // System.out.println("Record saved successfully!");

//         // String retrived_item = InvenLoader.Get_ItemQty(inventory_id,item_id);
//         // System.out.println(retrived_item);


//         //         // ✅ RSA signing and verification
//         // BigInteger[] keys = KeyLoader.loadKeys(inventory_id, "src/main/resources/keys.json");
//         // RSA rsa = new RSA(keys[0], keys[1], keys[2]);
//         // String message = item_id + " " + item_units + " " + item_price + " " + inventory_id;
//         // BigInteger signature = rsa.sign(message);
//         // boolean verified = rsa.verify(message, signature);

//         // boolean consensus = Consensus.verifyWithMajority(inventory_id, message, signature);
//         // if (consensus) {
//         //     RSA.save_record(item_id, item_units, item_price, inventory_id);
//         // }
//         Task3_KeyLoader.loadPKGValue("p");
//         BigInteger pp = new BigInteger("7919");
//         BigInteger qq = new BigInteger("7723");
//         BigInteger ee = new BigInteger("71");
//         // PKG pkg = new PKG((Task3_KeyLoader.loadPKGValue("p")),(Task3_KeyLoader.loadPKGValue("q")),(Task3_KeyLoader.loadPKGValue("e")));
//         PKG pkg = new PKG(pp,qq,ee);


//         System.out.println("G1 = " +pkg.generateSecretKey(new BigInteger("10")));
//         System.out.println("T1 = " + pkg.generateTJ(new BigInteger("23")));
//         BigInteger[] tjs = new BigInteger[3];
//         tjs[0] = new BigInteger("45623585");
//         tjs[1] = new BigInteger("51450053");
//         tjs[2] = new BigInteger("31563832");


//         System.out.println("Aggregated T = " + pkg.AggregatedT(tjs));
//         BigInteger G1 = pkg.generateSecretKey(new BigInteger("10"));
//         BigInteger R1 = new  BigInteger("23");
//         System.out.println("S1 = " + pkg.TESTgenerateSJ(G1,R1));
//         BigInteger[] sjs = new BigInteger[3];
//         sjs[0] = new BigInteger("15012078");
//         sjs[1] = new BigInteger("23498778");
//         sjs[2] = new BigInteger("53938548");
//         System.out.println("Agerated signature = " + pkg.aggregateSignatures(sjs));
//         System.out.println("The signature is s = (Agerated T,Agerated S) = " + 
//                 pkg.AggregatedT(tjs) + "," + pkg.aggregateSignatures(sjs));
//         BigInteger s =  pkg.aggregateSignatures(sjs);
//         BigInteger t = pkg.AggregatedT(tjs);
//         BigInteger[] ijs = new BigInteger[3];
//         ijs[0] = new BigInteger("10");
//         ijs[1] = new BigInteger("11");
//         ijs[2] = new BigInteger("12");

//         boolean isValid = pkg.TESTverifySignature(s, ijs, t);
//         System.out.println("Signature valid? " + isValid);
//     }
// }
