package com.rosary.Task3;

import java.math.BigInteger;

public class Task3_RSA {
    public BigInteger n, e, d;
    // genearate key pair
    public Task3_RSA(BigInteger p, BigInteger q, BigInteger e) {
        this.n = p.multiply(q);
        this.e = e;
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        this.d = e.modInverse(phi); // private exponent
    }

    public BigInteger encryptmsg(String message){
        BigInteger m = new BigInteger(message);
        BigInteger encrypted  = m.modPow(e, n);
        return encrypted;
    }

    public BigInteger decryptmsg(BigInteger ciphertext){
        BigInteger decrypted  = ciphertext.modPow(d, n);
        return decrypted;
    }

    // Verify S^e == (Π id_j) * T^H mod n
    public boolean verifyMultiSignature(BigInteger S, BigInteger[] ids, BigInteger T, String message, PKG pkg) {
        BigInteger hash = PKG.computeMd5OfTConcatM(T, message);
    
        BigInteger lhs = S.modPow(pkg.getPublicExponent(), pkg.getModulus());
    
        BigInteger idProduct = BigInteger.ONE;
        for (BigInteger id : ids) {
            idProduct = idProduct.multiply(id).mod(pkg.getModulus());
        }
    
        BigInteger rhs = idProduct.multiply(T.modPow(hash, pkg.getModulus())).mod(pkg.getModulus());
    
        System.out.println("LHS: " + lhs);
        System.out.println("RHS: " + rhs);
        return lhs.equals(rhs);
    }
}
