package ru.vsu.cs.automationFinanceBot.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class QRCodeDTO {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    private LocalDateTime dateTime;
    private float sum;
    private long fn;
    private int i;
    private long fp;
    private int n;

    public QRCodeDTO() {
    }

    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    public float getSum() {
        return this.sum;
    }

    public long getFn() {
        return this.fn;
    }

    public int getI() {
        return this.i;
    }

    public long getFp() {
        return this.fp;
    }

    public int getN() {
        return this.n;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }

    public void setFn(long fn) {
        this.fn = fn;
    }

    public void setI(int i) {
        this.i = i;
    }

    public void setFp(long fp) {
        this.fp = fp;
    }

    public void setN(int n) {
        this.n = n;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof QRCodeDTO)) return false;
        final QRCodeDTO other = (QRCodeDTO) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$dateTime = this.getDateTime();
        final Object other$dateTime = other.getDateTime();
        if (this$dateTime == null ? other$dateTime != null : !this$dateTime.equals(other$dateTime)) return false;
        if (Float.compare(this.getSum(), other.getSum()) != 0) return false;
        if (this.getFn() != other.getFn()) return false;
        if (this.getI() != other.getI()) return false;
        if (this.getFp() != other.getFp()) return false;
        if (this.getN() != other.getN()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof QRCodeDTO;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $dateTime = this.getDateTime();
        result = result * PRIME + ($dateTime == null ? 43 : $dateTime.hashCode());
        result = result * PRIME + Float.floatToIntBits(this.getSum());
        final long $fn = this.getFn();
        result = result * PRIME + (int) ($fn >>> 32 ^ $fn);
        result = result * PRIME + this.getI();
        final long $fp = this.getFp();
        result = result * PRIME + (int) ($fp >>> 32 ^ $fp);
        result = result * PRIME + this.getN();
        return result;
    }

    public String toString() {
        return "QRCode(dateTime=" + this.getDateTime() + ", sum=" + this.getSum() + ", fn=" + this.getFn() + ", i=" + this.getI() + ", fp=" + this.getFp() + ", n=" + this.getN() + ")";
    }
}
