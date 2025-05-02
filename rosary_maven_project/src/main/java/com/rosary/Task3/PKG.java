package com.rosary.Task3;

import java.math.BigInteger;
import java.security.MessageDigest;
public class PKG {
    private BigInteger n, e, d, phi;

    public PKG(BigInteger p, BigInteger q, BigInteger e){
        this.e = e; 
        this.n = p.multiply(q);
        this.phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        this.d = e.modInverse(phi); //Private key
    }
    
    // Generate secret key from identity
    public BigInteger generateSecretKey(BigInteger identity) {
        return identity.modPow(d,n);
    }
    // Public exponent (e)
    public BigInteger getPublicExponent() {
        return this.e;
    }

    // Modulus (n)
    public BigInteger getModulus() {
        return this.n;
    }
    public BigInteger getPrivateExponent() {
        return d;
    }

    public BigInteger generateTJ(BigInteger rj){
        return rj.modPow(e, n);
    }

    // Generate sj = gj * rj^H(t, m) mod n
    public BigInteger generateSJ(BigInteger gj, BigInteger rj, BigInteger t, String message) {
        try {
            BigInteger H = hashToBigInteger(t, message);
            BigInteger blinded = rj.modPow(H, n);
            return gj.multiply(blinded).mod(n);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }


    public BigInteger TESTgenerateSJ(BigInteger gj, BigInteger rj) {

            BigInteger H = new BigInteger("1000");
            BigInteger blinded = rj.modPow(H, n);
            return gj.multiply(blinded).mod(n);

    }

    // H(t, m) = SHA-256(t || m) as BigInteger
    public BigInteger hashToBigInteger(BigInteger t, String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] tBytes = t.toString().getBytes("UTF-8");
            byte[] mBytes = message.getBytes("UTF-8");
    
            byte[] combined = new byte[tBytes.length + mBytes.length];
            System.arraycopy(tBytes, 0, combined, 0, tBytes.length);
            System.arraycopy(mBytes, 0, combined, tBytes.length, mBytes.length);
    
            byte[] hash = digest.digest(combined);
            return new BigInteger(1, hash); // ensure non-negative
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed: " + e.getMessage(), e);
        }
    }

    
    
    // Combine all partial signatures into one multisignature
    public BigInteger AggregatedT (BigInteger[] tjs) {
        BigInteger t = BigInteger.ONE;
        for (BigInteger tj : tjs) {
            t = t.multiply(tj).mod(n);
        }
        return t;
    }

    public BigInteger aggregateSignatures(BigInteger[] sjs) {
        BigInteger s = BigInteger.ONE;
        for (BigInteger sj : sjs) {
            s = s.multiply(sj).mod(n); // n is RSA modulus
        }
        return s;
    }

    public boolean verifySignature(BigInteger s, BigInteger[] ijs, BigInteger t, String  m,BigInteger hashValue) {
        // Left-hand side: s^e mod n
        BigInteger lhs = s.modPow(e, n);
    
        // Compute ∏ij
        BigInteger ijProduct = BigInteger.ONE;
        for (BigInteger ij : ijs) {
            ijProduct = ijProduct.multiply(ij).mod(n);
        }
    
        // Right-hand side: (∏ij) * t^H(t,m) mod n
        BigInteger rhs = ijProduct.multiply(t.modPow(hashValue, n)).mod(n);
    
        // Compare both sides
        return lhs.equals(rhs);
    }
    // public boolean TESTverifySignature(BigInteger s, BigInteger[] ijs, BigInteger t) {
    //     // Left-hand side: s^e mod n
    //     BigInteger lhs = s.modPow(e, n);
    
    //     // Compute ∏ij
    //     BigInteger ijProduct = BigInteger.ONE;
    //     for (BigInteger ij : ijs) {
    //         ijProduct = ijProduct.multiply(ij).mod(n);
    //     }
    
    //     // Compute t^H(t, m) mod n
    //     BigInteger tExp = t.modPow(new BigInteger("1000"), n);

    //     // Compute right side: (∏ij) * t^H(t, m) mod n
    //     BigInteger rhs = ijProduct.multiply(tExp).mod(n);

    //     // Compare
    //     return lhs.equals(rhs);
    // }

    // public BigInteger getHashForVerification(BigInteger t, String m) throws NoSuchAlgorithmException {
    //     return hashToBigInteger(t, m);
    // }
    

    /**
     * Returns the MD5 hash of a string as a hexadecimal string.
     */
    public  String md5ToHex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 hashing failed: " + e.getMessage(), e);
        }
    }

    // public  BigInteger md5ToDecimal(String input) {
    //     try {
    //         MessageDigest md = MessageDigest.getInstance("MD5");
    //         byte[] digest = md.digest(input.getBytes("UTF-8"));
    //         return new BigInteger(1, digest); // non-negative BigInteger
    //     } catch (Exception e) {
    //         throw new RuntimeException("MD5 hashing failed: " + e.getMessage(), e);
    //     }
    // }

    public  static BigInteger computeMd5OfTConcatM(BigInteger t, String m) {
        try {
            // Concatenate t as string with message string
            String input = t + m;

            // Compute MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes("UTF-8"));

            // Return digest as positive BigInteger
            return new BigInteger(1, digest);
        } catch (Exception e) {
            throw new RuntimeException("MD5 hash failed: " + e.getMessage(), e);
        }
    }

}
