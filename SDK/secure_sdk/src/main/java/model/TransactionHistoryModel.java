package model;

/**
 * Created by linchpin on 31/5/17.
 */

public class TransactionHistoryModel {
    private Txs[] items;

    private String totalItems;

    public Txs[] getItems() {
        return items;
    }

    public void setItems(Txs[] items) {
        this.items = items;
    }

    public String getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(String totalItems) {
        this.totalItems = totalItems;
    }

    public class Txs
    {
        private String confirmations;

        private String locktime;

        private Vout[] vout;

        private String blockhash;

        private String fees;

        private String blocktime;

        private String version;

        private String size;

        private String blockheight;

        private String valueIn;

        private String time;

        private Vin[] vin;

        private String txid;

        private String valueOut;

        public String getConfirmations ()
        {
            return confirmations;
        }

        public void setConfirmations (String confirmations)
        {
            this.confirmations = confirmations;
        }

        public String getLocktime ()
        {
            return locktime;
        }

        public void setLocktime (String locktime)
        {
            this.locktime = locktime;
        }

        public Vout[] getVout ()
        {
            return vout;
        }

        public void setVout (Vout[] vout)
        {
            this.vout = vout;
        }

        public String getBlockhash ()
        {
            return blockhash;
        }

        public void setBlockhash (String blockhash)
        {
            this.blockhash = blockhash;
        }

        public String getFees ()
        {
            return fees;
        }

        public void setFees (String fees)
        {
            this.fees = fees;
        }

        public String getBlocktime ()
        {
            return blocktime;
        }

        public void setBlocktime (String blocktime)
        {
            this.blocktime = blocktime;
        }

        public String getVersion ()
        {
            return version;
        }

        public void setVersion (String version)
        {
            this.version = version;
        }

        public String getSize ()
        {
            return size;
        }

        public void setSize (String size)
        {
            this.size = size;
        }

        public String getBlockheight ()
        {
            return blockheight;
        }

        public void setBlockheight (String blockheight)
        {
            this.blockheight = blockheight;
        }

        public String getValueIn ()
        {
            return valueIn;
        }

        public void setValueIn (String valueIn)
        {
            this.valueIn = valueIn;
        }

        public String getTime ()
        {
            return time;
        }

        public void setTime (String time)
        {
            this.time = time;
        }

        public Vin[] getVin ()
        {
            return vin;
        }

        public void setVin (Vin[] vin)
        {
            this.vin = vin;
        }

        public String getTxid ()
        {
            return txid;
        }

        public void setTxid (String txid)
        {
            this.txid = txid;
        }

        public String getValueOut ()
        {
            return valueOut;
        }

        public void setValueOut (String valueOut)
        {
            this.valueOut = valueOut;
        }

    }
    public class Vin
    {
        private String doubleSpentTxID;

        private String valueSat;

        private String sequence;

        private String value;

        private String n;

        private String vout;

        private String txid;

        private String addr;

        private ScriptSig scriptSig;

        public String getDoubleSpentTxID ()
        {
            return doubleSpentTxID;
        }

        public void setDoubleSpentTxID (String doubleSpentTxID)
        {
            this.doubleSpentTxID = doubleSpentTxID;
        }

        public String getValueSat ()
        {
            return valueSat;
        }

        public void setValueSat (String valueSat)
        {
            this.valueSat = valueSat;
        }

        public String getSequence ()
        {
            return sequence;
        }

        public void setSequence (String sequence)
        {
            this.sequence = sequence;
        }

        public String getValue ()
        {
            return value;
        }

        public void setValue (String value)
        {
            this.value = value;
        }

        public String getN ()
        {
            return n;
        }

        public void setN (String n)
        {
            this.n = n;
        }

        public String getVout ()
        {
            return vout;
        }

        public void setVout (String vout)
        {
            this.vout = vout;
        }

        public String getTxid ()
        {
            return txid;
        }

        public void setTxid (String txid)
        {
            this.txid = txid;
        }

        public String getAddr ()
        {
            return addr;
        }

        public void setAddr (String addr)
        {
            this.addr = addr;
        }

        public ScriptSig getScriptSig ()
        {
            return scriptSig;
        }

        public void setScriptSig (ScriptSig scriptSig)
        {
            this.scriptSig = scriptSig;
        }

    }


    public class ScriptPubKey
    {
        private String hex;

        private String type;

        private String[] addresses;

        private String asm;

        public String getHex ()
        {
            return hex;
        }

        public void setHex (String hex)
        {
            this.hex = hex;
        }

        public String getType ()
        {
            return type;
        }

        public void setType (String type)
        {
            this.type = type;
        }

        public String[] getAddresses ()
        {
            return addresses;
        }

        public void setAddresses (String[] addresses)
        {
            this.addresses = addresses;
        }

        public String getAsm ()
        {
            return asm;
        }

        public void setAsm (String asm)
        {
            this.asm = asm;
        }

    }
    public class ScriptSig
    {
        private String hex;

        private String asm;

        public String getHex ()
        {
            return hex;
        }

        public void setHex (String hex)
        {
            this.hex = hex;
        }

        public String getAsm ()
        {
            return asm;
        }

        public void setAsm (String asm)
        {
            this.asm = asm;
        }
    }
    public class Vout
    {
        private int spentIndex;

        private ScriptPubKey scriptPubKey;

        private String spentTxId;

        private String n;

        private String value;

        private long spentHeight;

        public int getSpentIndex ()
        {
            return spentIndex;
        }

        public void setSpentIndex (int spentIndex)
        {
            this.spentIndex = spentIndex;
        }

        public ScriptPubKey getScriptPubKey ()
        {
            return scriptPubKey;
        }

        public void setScriptPubKey (ScriptPubKey scriptPubKey)
        {
            this.scriptPubKey = scriptPubKey;
        }

        public String getSpentTxId ()
        {
            return spentTxId;
        }

        public void setSpentTxId (String spentTxId)
        {
            this.spentTxId = spentTxId;
        }

        public String getN ()
        {
            return n;
        }

        public void setN (String n)
        {
            this.n = n;
        }

        public String getValue ()
        {
            return value;
        }

        public void setValue (String value)
        {
            this.value = value;
        }

        public long getSpentHeight ()
        {
            return spentHeight;
        }

        public void setSpentHeight (long spentHeight)
        {
            this.spentHeight = spentHeight;
        }

    }
}
