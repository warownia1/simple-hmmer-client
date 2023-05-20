package uk.ac.dundee.compbio.hmmerclient;

import java.io.Reader;
import java.util.Objects;

import static uk.ac.dundee.compbio.hmmerclient.ExceptionUtils.newIAE;
import static uk.ac.dundee.compbio.hmmerclient.ExceptionUtils.newISE;

public class PhmmerRequest {

  public static class Builder {
    private Float incE;
    private Float incdomE;
    private Float E;
    private Float domE;
    private Float incT;
    private Float incdomT;
    private Float T;
    private Float domT;
    private Float popen;
    private Float pextend;
    private SubstitutionMatrix mx = SubstitutionMatrix.BLOSUM62;
    private boolean noBias = false;
    private boolean compressedOut = false;
    private boolean alignView = true;
    private SequenceDatabase database = SequenceDatabase.UNIPROTKB;
    private Float evalue;
    private Reader sequence;
    private Integer nhits;

    public Float incE() {
      return incE;
    }

    public Builder incE(Float incE) {
      if (incE != null && (incE <= 0 || incE > 10))
        throw newIAE("incE must be greater than 0 and less or equal to 10");
      this.incE = incE;
      return this;
    }

    public Float incdomE() {
      return incdomE;
    }

    public Builder incdomE(Float incdomE) {
      if (incdomE != null && (incdomE <= 0 || incdomE > 10))
        throw newIAE("incdomE must be greater than 0 and less or equal to 10");
      this.incdomE = incdomE;
      return this;
    }

    public Float E() {
      return E;
    }

    public Builder E(Float E) {
      if (E != null && (E <= 0 || E > 10))
        throw newIAE("E must be greater than 0 and less or equal to 10");
      this.E = E;
      return this;
    }

    public Float getDomE() {
      return domE;
    }

    public Builder domE(Float domE) {
      if (domE != null && (domE <= 0 || domE > 10))
        throw newIAE("domE must be greater than 0 and less or equal to 10.");
      this.domE = domE;
      return this;
    }

    public Float incT() {
      return incT;
    }

    public Builder incT(Float incT) {
      if (incT != null && incT <= 0)
        throw newIAE("incT must be greater than 0.");
      this.incT = incT;
      return this;
    }

    public Float incdomT() {
      return incdomT;
    }

    public Builder incdomT(Float incdomT) {
      if (incdomT != null && incdomT <= 0)
        throw newIAE("incdomT must be greater than 0.");
      this.incdomT = incdomT;
      return this;
    }

    public Float T() {
      return T;
    }

    public Builder T(Float T) {
      if (T != null && T <= 0)
        throw newIAE("T must be greater than 0.");
      this.T = T;
      return this;
    }

    public Float domT() {
      return domT;
    }

    public Builder domT(Float domT) {
      if (domT != null && domT <= 0)
        throw newIAE("domT must be greater than 0.");
      this.domT = domT;
      return this;
    }

    public Float popen() {
      return popen;
    }

    public Builder popen(Float popen) {
      if (popen != null && (popen < 0 || popen >= 0.5f))
        throw newIAE("popen must be greater or equal to 0 and less than 0.5.");
      this.popen = popen;
      return this;
    }

    public Float pextend() {
      return pextend;
    }

    public Builder pextend(Float pextend) {
      if (pextend != null && (pextend < 0 || pextend >= 1))
        throw newIAE("pextend must be greater or equal to 0 and less than 1.");
      this.pextend = pextend;
      return this;
    }

    public SubstitutionMatrix mx() {
      return mx;
    }

    public Builder mx(SubstitutionMatrix mx) {
      this.mx = mx;
      return this;
    }

    public Boolean noBias() {
      return noBias;
    }

    public Builder noBias(boolean noBias) {
      this.noBias = noBias;
      return this;
    }

    public Boolean compressedOut() {
      return compressedOut;
    }

    public Builder compressedOut(boolean compressedOut) {
      this.compressedOut = compressedOut;
      return this;
    }

    public Boolean alignView() {
      return alignView;
    }

    public Builder alignView(boolean alignView) {
      this.alignView = alignView;
      return this;
    }

    public SequenceDatabase database() {
      return database;
    }

    public Builder database(SequenceDatabase database) {
      this.database = database;
      return this;
    }

    public Float evalue() {
      return evalue;
    }

    public Builder evalue(Float evalue) {
      this.evalue = evalue;
      return this;
    }

    public Reader sequence() {
      return sequence;
    }

    public Builder sequence(Reader sequence) {
      Objects.requireNonNull(sequence);
      this.sequence = sequence;
      return this;
    }

    public Integer nhits() {
      return nhits;
    }

    public Builder nhits(Integer nhits) {
      this.nhits = nhits;
      return this;
    }

    public PhmmerRequest build() {
      if (sequence == null)
        throw newISE("sequence not set");
      boolean usingE = incE != null || incdomE != null || E != null || domE != null;
      boolean usingT = incT != null || incdomT != null || T != null || domT != null;
      if (usingE && usingT)
        throw newISE("using both E-value and bit scores is not allowed");
      return new PhmmerRequest(this);
    }
  }

  public enum SubstitutionMatrix {
    BLOSUM45("BLOSUM45"),
    BLOSUM62("BLOSUM62"),
    BLOSUM90("BLOSUM90"),
    PAM30("PAM30"),
    PAM70("PAM70");

    final String strvalue;

    SubstitutionMatrix(String value) {
      this.strvalue = value;
    }
  }

  public enum SequenceDatabase {
    SWISS_PROT("swissprot"),
    REFERENCE_PROTEOMES("uniprotrefprot"),
    UNIPROTKB("uniprotkb"),
    PDB("pdb"),
    RP75("rp75"),
    RP55("rp55"),
    RP35("rp35"),
    RP15("rp15"),
    ENSEMBL("ensembl"),
    MEROPS("merops"),
    QUEST_FOR_ORTHOLOGS("qfo"),
    CHEMBL("chembl");

    final String strvalue;

    SequenceDatabase(String value) {
      this.strvalue = value;
    }
  }

  private final Float incE;
  private final Float incdomE;
  private final Float E;
  private final Float domE;
  private final Float incT;
  private final Float incdomT;
  private final Float T;
  private final Float domT;
  private final Float popen;
  private final Float pextend;
  private final SubstitutionMatrix mx;
  private final boolean noBias;
  private final boolean compressedOut;
  private final boolean alignView;
  private final SequenceDatabase database;
  private final Float evalue;
  private final Reader sequence;
  private final Integer nhits;

  public static PhmmerRequest.Builder newBuilder() {
    return new Builder();
  }

  private PhmmerRequest(Builder builder) {
    incE = builder.incE;
    incdomE = builder.incdomE;
    E = builder.E;
    domE = builder.domE;
    incT = builder.incT;
    incdomT = builder.incdomT;
    T = builder.T;
    domT = builder.domT;
    popen = builder.popen;
    pextend = builder.pextend;
    mx = builder.mx;
    noBias = builder.noBias;
    compressedOut = builder.compressedOut;
    alignView = builder.alignView;
    database = builder.database;
    evalue = builder.evalue;
    sequence = builder.sequence;
    nhits = builder.nhits;
  }

  public Float getIncE() {
    return incE;
  }

  public Float getIncdomE() {
    return incdomE;
  }

  public Float getE() {
    return E;
  }

  public Float getDomE() {
    return domE;
  }

  public Float getIncT() {
    return incT;
  }

  public Float getIncdomT() {
    return incdomT;
  }

  public Float getT() {
    return T;
  }

  public Float getDomT() {
    return domT;
  }

  public Float getPopen() {
    return popen;
  }

  public Float getPextend() {
    return pextend;
  }

  public SubstitutionMatrix getMx() {
    return mx;
  }

  public boolean getNoBias() {
    return noBias;
  }

  public boolean getCompressedOut() {
    return compressedOut;
  }

  public boolean getAlignView() {
    return alignView;
  }

  public SequenceDatabase getDatabase() {
    return database;
  }

  public Float getEvalue() {
    return evalue;
  }

  public Reader getSequence() {
    return sequence;
  }

  public Integer getNhits() {
    return nhits;
  }

}
