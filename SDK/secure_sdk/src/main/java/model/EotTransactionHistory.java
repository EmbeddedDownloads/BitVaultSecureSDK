package model;

/**
 * Created by linchpin on 24/11/17.
 */

public class EotTransactionHistory {
    private Txs[] txs;

    private String pagesTotal;

    public Txs[] getTxs ()
    {
        return txs;
    }

    public void setTxs (Txs[] txs)
    {
        this.txs = txs;
    }

    public String getPagesTotal ()
    {
        return pagesTotal;
    }

    public void setPagesTotal (String pagesTotal)
    {
        this.pagesTotal = pagesTotal;
    }

    public class Txs {
        private String valueIn;

        private String locktime;

        private Vin[] vin;

        private Vout[] vout;

        private String txid;

        private String fees;

        private String size;

        private String valueOut;

        private String version;

        public String getValueIn() {
            return valueIn;
        }

        public void setValueIn(String valueIn) {
            this.valueIn = valueIn;
        }

        public String getLocktime() {
            return locktime;
        }

        public void setLocktime(String locktime) {
            this.locktime = locktime;
        }

        public Vin[] getVin() {
            return vin;
        }

        public void setVin(Vin[] vin) {
            this.vin = vin;
        }

        public Vout[] getVout() {
            return vout;
        }

        public void setVout(Vout[] vout) {
            this.vout = vout;
        }

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public String getFees() {
            return fees;
        }

        public void setFees(String fees) {
            this.fees = fees;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getValueOut() {
            return valueOut;
        }

        public void setValueOut(String valueOut) {
            this.valueOut = valueOut;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    public class Vout {
        private ScriptPubKey scriptPubKey;

        private String n;

        private String value;

        public ScriptPubKey getScriptPubKey() {
            return scriptPubKey;
        }

        public void setScriptPubKey(ScriptPubKey scriptPubKey) {
            this.scriptPubKey = scriptPubKey;
        }

        public String getN() {
            return n;
        }

        public void setN(String n) {
            this.n = n;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public class ScriptPubKey {
        private String reqSigs;

        private String[] addresses;

        private String type;

        private String asm;

        public String getReqSigs() {
            return reqSigs;
        }

        public void setReqSigs(String reqSigs) {
            this.reqSigs = reqSigs;
        }

        public String[] getAddresses() {
            return addresses;
        }

        public void setAddresses(String[] addresses) {
            this.addresses = addresses;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAsm() {
            return asm;
        }

        public void setAsm(String asm) {
            this.asm = asm;
        }
    }

    public class Vin {
        private String doubleSpentTxID;

        private String valueSat;

        private String sequence;

        private String value;

        private String n;

        private String vout;

        private String txid;

        private String addr;

        private ScriptSig scriptSig;



        public String getDoubleSpentTxID() {
            return doubleSpentTxID;
        }

        public void setDoubleSpentTxID(String doubleSpentTxID) {
            this.doubleSpentTxID = doubleSpentTxID;
        }

        public String getValueSat() {
            return valueSat;
        }

        public void setValueSat(String valueSat) {
            this.valueSat = valueSat;
        }

        public String getSequence() {
            return sequence;
        }

        public void setSequence(String sequence) {
            this.sequence = sequence;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getN() {
            return n;
        }

        public void setN(String n) {
            this.n = n;
        }

        public String getVout() {
            return vout;
        }

        public void setVout(String vout) {
            this.vout = vout;
        }

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public ScriptSig getScriptSig() {
            return scriptSig;
        }

        public void setScriptSig(ScriptSig scriptSig) {
            this.scriptSig = scriptSig;
        }
    }

    public class ScriptSig {
        private String asm;

        public String getAsm() {
            return asm;
        }

        public void setAsm(String asm) {
            this.asm = asm;
        }
    }
    }
