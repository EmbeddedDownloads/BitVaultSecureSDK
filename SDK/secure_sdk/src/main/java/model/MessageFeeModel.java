package model;

/**
 * Created by Vinod Singh on 18/5/17.
 */

public class MessageFeeModel {
    private Fees fees;

    public Fees getFees ()
    {
        return fees;
    }

    public void setFees (Fees fees)
    {
        this.fees = fees;
    }

    public class Fees {
        private String receiver_fee;

        private String msg_char_fee;

        private Media_fee media_fee;

        public String getReceiver_fee() {
            return receiver_fee;
        }

        public void setReceiver_fee(String receiver_fee) {
            this.receiver_fee = receiver_fee;
        }

        public String getMsg_char_fee() {
            return msg_char_fee;
        }

        public void setMsg_char_fee(String msg_char_fee) {
            this.msg_char_fee = msg_char_fee;
        }

        public Media_fee getMedia_fee() {
            return media_fee;
        }

        public void setMedia_fee(Media_fee media_fee) {
            this.media_fee = media_fee;
        }

        public class Media_fee {
            private String _4MB;

            private String _8MB;

            private String _10MB;

            private String _6MB;

            private String _2MB;

            public String get_4MB() {
                return _4MB;
            }

            public void set_4MB(String _4MB) {
                this._4MB = _4MB;
            }

            public String get_8MB() {
                return _8MB;
            }

            public void set_8MB(String _8MB) {
                this._8MB = _8MB;
            }

            public String get_10MB() {
                return _10MB;
            }

            public void set_10MB(String _10MB) {
                this._10MB = _10MB;
            }

            public String get_6MB() {
                return _6MB;
            }

            public void set_6MB(String _6MB) {
                this._6MB = _6MB;
            }

            public String get_2MB() {
                return _2MB;
            }

            public void set_2MB(String _2MB) {
                this._2MB = _2MB;
            }
        }
        }
}
