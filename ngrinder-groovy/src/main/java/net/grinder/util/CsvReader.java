//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.grinder.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.HashMap;

public class CsvReader {
	private Reader inputStream;
	private String fileName;
	private UserSettings userSettings;
	private Charset charset;
	private boolean useCustomRecordDelimiter;
	private DataBuffer dataBuffer;
	private ColumnBuffer columnBuffer;
	private RawRecordBuffer rawBuffer;
	private boolean[] isQualified;
	private String rawRecord;
	private HeadersHolder headersHolder;
	private boolean startedColumn;
	private boolean startedWithQualifier;
	private boolean hasMoreData;
	private char lastLetter;
	private boolean hasReadNextLine;
	private int columnsCount;
	private long currentRecord;
	private String[] values;
	private boolean initialized;
	private boolean closed;
	public static final int ESCAPE_MODE_DOUBLED = 1;
	public static final int ESCAPE_MODE_BACKSLASH = 2;

	public CsvReader(String var1, char var2, Charset var3) throws FileNotFoundException {
		this.inputStream = null;
		this.fileName = null;
		this.userSettings = new UserSettings();
		this.charset = null;
		this.useCustomRecordDelimiter = false;
		this.dataBuffer = new DataBuffer();
		this.columnBuffer = new ColumnBuffer();
		this.rawBuffer = new RawRecordBuffer();
		this.isQualified = null;
		this.rawRecord = "";
		this.headersHolder = new HeadersHolder();
		this.startedColumn = false;
		this.startedWithQualifier = false;
		this.hasMoreData = true;
		this.lastLetter = 0;
		this.hasReadNextLine = false;
		this.columnsCount = 0;
		this.currentRecord = 0L;
		this.values = new String[10];
		this.initialized = false;
		this.closed = false;
		if (var1 == null) {
			throw new IllegalArgumentException("Parameter fileName can not be null.");
		} else if (var3 == null) {
			throw new IllegalArgumentException("Parameter charset can not be null.");
		} else if (!(new File(var1)).exists()) {
			throw new FileNotFoundException("File " + var1 + " does not exist.");
		} else {
			this.fileName = var1;
			this.userSettings.Delimiter = var2;
			this.charset = var3;
			this.isQualified = new boolean[this.values.length];
		}
	}

	public CsvReader(String var1, char var2) throws FileNotFoundException {
		this(var1, var2, Charset.forName("ISO-8859-1"));
	}

	public CsvReader(String var1) throws FileNotFoundException {
		this(var1, ',');
	}

	public CsvReader(Reader var1, char var2) {
		this.inputStream = null;
		this.fileName = null;
		this.userSettings = new UserSettings();
		this.charset = null;
		this.useCustomRecordDelimiter = false;
		this.dataBuffer = new DataBuffer();
		this.columnBuffer = new ColumnBuffer();
		this.rawBuffer = new RawRecordBuffer();
		this.isQualified = null;
		this.rawRecord = "";
		this.headersHolder = new HeadersHolder();
		this.startedColumn = false;
		this.startedWithQualifier = false;
		this.hasMoreData = true;
		this.lastLetter = 0;
		this.hasReadNextLine = false;
		this.columnsCount = 0;
		this.currentRecord = 0L;
		this.values = new String[10];
		this.initialized = false;
		this.closed = false;
		if (var1 == null) {
			throw new IllegalArgumentException("Parameter inputStream can not be null.");
		} else {
			this.inputStream = var1;
			this.userSettings.Delimiter = var2;
			this.initialized = true;
			this.isQualified = new boolean[this.values.length];
		}
	}

	public CsvReader(Reader var1) {
		this(var1, ',');
	}

	public CsvReader(InputStream var1, char var2, Charset var3) {
		this((Reader)(new InputStreamReader(var1, var3)), var2);
	}

	public CsvReader(InputStream var1, Charset var2) {
		this((Reader)(new InputStreamReader(var1, var2)));
	}

	public boolean getCaptureRawRecord() {
		return this.userSettings.CaptureRawRecord;
	}

	public void setCaptureRawRecord(boolean var1) {
		this.userSettings.CaptureRawRecord = var1;
	}

	public String getRawRecord() {
		return this.rawRecord;
	}

	public boolean getTrimWhitespace() {
		return this.userSettings.TrimWhitespace;
	}

	public void setTrimWhitespace(boolean var1) {
		this.userSettings.TrimWhitespace = var1;
	}

	public char getDelimiter() {
		return this.userSettings.Delimiter;
	}

	public void setDelimiter(char var1) {
		this.userSettings.Delimiter = var1;
	}

	public char getRecordDelimiter() {
		return this.userSettings.RecordDelimiter;
	}

	public void setRecordDelimiter(char var1) {
		this.useCustomRecordDelimiter = true;
		this.userSettings.RecordDelimiter = var1;
	}

	public char getTextQualifier() {
		return this.userSettings.TextQualifier;
	}

	public void setTextQualifier(char var1) {
		this.userSettings.TextQualifier = var1;
	}

	public boolean getUseTextQualifier() {
		return this.userSettings.UseTextQualifier;
	}

	public void setUseTextQualifier(boolean var1) {
		this.userSettings.UseTextQualifier = var1;
	}

	public char getComment() {
		return this.userSettings.Comment;
	}

	public void setComment(char var1) {
		this.userSettings.Comment = var1;
	}

	public boolean getUseComments() {
		return this.userSettings.UseComments;
	}

	public void setUseComments(boolean var1) {
		this.userSettings.UseComments = var1;
	}

	public int getEscapeMode() {
		return this.userSettings.EscapeMode;
	}

	public void setEscapeMode(int var1) throws IllegalArgumentException {
		if (var1 != 1 && var1 != 2) {
			throw new IllegalArgumentException("Parameter escapeMode must be a valid value.");
		} else {
			this.userSettings.EscapeMode = var1;
		}
	}

	public boolean getSkipEmptyRecords() {
		return this.userSettings.SkipEmptyRecords;
	}

	public void setSkipEmptyRecords(boolean var1) {
		this.userSettings.SkipEmptyRecords = var1;
	}

	public boolean getSafetySwitch() {
		return this.userSettings.SafetySwitch;
	}

	public void setSafetySwitch(boolean var1) {
		this.userSettings.SafetySwitch = var1;
	}

	public int getColumnCount() {
		return this.columnsCount;
	}

	public long getCurrentRecord() {
		return this.currentRecord - 1L;
	}

	public int getHeaderCount() {
		return this.headersHolder.Length;
	}

	public String[] getHeaders() throws IOException {
		this.checkClosed();
		if (this.headersHolder.Headers == null) {
			return null;
		} else {
			String[] var1 = new String[this.headersHolder.Length];
			System.arraycopy(this.headersHolder.Headers, 0, var1, 0, this.headersHolder.Length);
			return var1;
		}
	}

	public void setHeaders(String[] var1) {
		this.headersHolder.Headers = var1;
		this.headersHolder.IndexByName.clear();
		if (var1 != null) {
			this.headersHolder.Length = var1.length;
		} else {
			this.headersHolder.Length = 0;
		}

		for(int var2 = 0; var2 < this.headersHolder.Length; ++var2) {
			this.headersHolder.IndexByName.put(var1[var2], new Integer(var2));
		}

	}

	public String[] getValues() throws IOException {
		this.checkClosed();
		String[] var1 = new String[this.columnsCount];
		System.arraycopy(this.values, 0, var1, 0, this.columnsCount);
		return var1;
	}

	public String get(int var1) throws IOException {
		this.checkClosed();
		return var1 > -1 && var1 < this.columnsCount ? this.values[var1] : "";
	}

	public String get(String var1) throws IOException {
		this.checkClosed();
		return this.get(this.getIndex(var1));
	}

	public static CsvReader parse(String var0) {
		if (var0 == null) {
			throw new IllegalArgumentException("Parameter data can not be null.");
		} else {
			return new CsvReader(new StringReader(var0));
		}
	}

	public boolean readRecord() throws IOException {
		this.checkClosed();
		this.columnsCount = 0;
		this.rawBuffer.Position = 0;
		this.dataBuffer.LineStart = this.dataBuffer.Position;
		this.hasReadNextLine = false;
		if (this.hasMoreData) {
			while(true) {
				if (this.dataBuffer.Position == this.dataBuffer.Count) {
					this.checkDataLength();
				} else {
					this.startedWithQualifier = false;
					char var1 = this.dataBuffer.Buffer[this.dataBuffer.Position];
					boolean var2;
					if (this.userSettings.UseTextQualifier && var1 == this.userSettings.TextQualifier) {
						this.lastLetter = var1;
						this.startedColumn = true;
						this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
						this.startedWithQualifier = true;
						var2 = false;
						char var10 = this.userSettings.TextQualifier;
						if (this.userSettings.EscapeMode == 2) {
							var10 = '\\';
						}

						boolean var11 = false;
						boolean var12 = false;
						boolean var14 = false;
						byte var13 = 1;
						int var8 = 0;
						char var9 = 0;
						++this.dataBuffer.Position;

						do {
							if (this.dataBuffer.Position == this.dataBuffer.Count) {
								this.checkDataLength();
							} else {
								var1 = this.dataBuffer.Buffer[this.dataBuffer.Position];
								if (var11) {
									this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
									if (var1 == this.userSettings.Delimiter) {
										this.endColumn();
									} else if (!this.useCustomRecordDelimiter && (var1 == '\r' || var1 == '\n') || this.useCustomRecordDelimiter && var1 == this.userSettings.RecordDelimiter) {
										this.endColumn();
										this.endRecord();
									}
								} else if (var14) {
									++var8;
									switch(var13) {
										case 1:
											var9 = (char)(var9 * 16);
											var9 += hexToDec(var1);
											if (var8 == 4) {
												var14 = false;
											}
											break;
										case 2:
											var9 = (char)(var9 * 8);
											var9 += (char)(var1 - 48);
											if (var8 == 3) {
												var14 = false;
											}
											break;
										case 3:
											var9 = (char)(var9 * 10);
											var9 += (char)(var1 - 48);
											if (var8 == 3) {
												var14 = false;
											}
											break;
										case 4:
											var9 = (char)(var9 * 16);
											var9 += hexToDec(var1);
											if (var8 == 2) {
												var14 = false;
											}
									}

									if (!var14) {
										this.appendLetter(var9);
									} else {
										this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
									}
								} else if (var1 == this.userSettings.TextQualifier) {
									if (var12) {
										var12 = false;
										var2 = false;
									} else {
										this.updateCurrentValue();
										if (this.userSettings.EscapeMode == 1) {
											var12 = true;
										}

										var2 = true;
									}
								} else if (this.userSettings.EscapeMode == 2 && var12) {
									switch(var1) {
										case '0':
										case '1':
										case '2':
										case '3':
										case '4':
										case '5':
										case '6':
										case '7':
											var13 = 2;
											var14 = true;
											var8 = 1;
											var9 = (char)(var1 - 48);
											this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
										case '8':
										case '9':
										case ':':
										case ';':
										case '<':
										case '=':
										case '>':
										case '?':
										case '@':
										case 'A':
										case 'B':
										case 'C':
										case 'E':
										case 'F':
										case 'G':
										case 'H':
										case 'I':
										case 'J':
										case 'K':
										case 'L':
										case 'M':
										case 'N':
										case 'P':
										case 'Q':
										case 'R':
										case 'S':
										case 'T':
										case 'V':
										case 'W':
										case 'Y':
										case 'Z':
										case '[':
										case '\\':
										case ']':
										case '^':
										case '_':
										case '`':
										case 'c':
										case 'g':
										case 'h':
										case 'i':
										case 'j':
										case 'k':
										case 'l':
										case 'm':
										case 'p':
										case 'q':
										case 's':
										case 'w':
										default:
											break;
										case 'D':
										case 'O':
										case 'U':
										case 'X':
										case 'd':
										case 'o':
										case 'u':
										case 'x':
											switch(var1) {
												case 'D':
												case 'd':
													var13 = 3;
													break;
												case 'O':
												case 'o':
													var13 = 2;
													break;
												case 'U':
												case 'u':
													var13 = 1;
													break;
												case 'X':
												case 'x':
													var13 = 4;
											}

											var14 = true;
											var8 = 0;
											var9 = 0;
											this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
											break;
										case 'a':
											this.appendLetter('\u0007');
											break;
										case 'b':
											this.appendLetter('\b');
											break;
										case 'e':
											this.appendLetter('\u001b');
											break;
										case 'f':
											this.appendLetter('\f');
											break;
										case 'n':
											this.appendLetter('\n');
											break;
										case 'r':
											this.appendLetter('\r');
											break;
										case 't':
											this.appendLetter('\t');
											break;
										case 'v':
											this.appendLetter('\u000b');
									}

									var12 = false;
								} else if (var1 == var10) {
									this.updateCurrentValue();
									var12 = true;
								} else if (var2) {
									if (var1 == this.userSettings.Delimiter) {
										this.endColumn();
									} else if ((this.useCustomRecordDelimiter || var1 != '\r' && var1 != '\n') && (!this.useCustomRecordDelimiter || var1 != this.userSettings.RecordDelimiter)) {
										this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
										var11 = true;
									} else {
										this.endColumn();
										this.endRecord();
									}

									var2 = false;
								}

								this.lastLetter = var1;
								if (this.startedColumn) {
									++this.dataBuffer.Position;
									if (this.userSettings.SafetySwitch && this.dataBuffer.Position - this.dataBuffer.ColumnStart + this.columnBuffer.Position > 100000) {
										this.close();
										throw new IOException("Maximum column length of 100,000 exceeded in column " + NumberFormat.getIntegerInstance().format((long)this.columnsCount) + " in record " + NumberFormat.getIntegerInstance().format(this.currentRecord) + ". Set the SafetySwitch property to false" + " if you're expecting column lengths greater than 100,000 characters to" + " avoid this error.");
									}
								}
							}
						} while(this.hasMoreData && this.startedColumn);
					} else if (var1 == this.userSettings.Delimiter) {
						this.lastLetter = var1;
						this.endColumn();
					} else if (this.useCustomRecordDelimiter && var1 == this.userSettings.RecordDelimiter) {
						if (!this.startedColumn && this.columnsCount <= 0 && this.userSettings.SkipEmptyRecords) {
							this.dataBuffer.LineStart = this.dataBuffer.Position + 1;
						} else {
							this.endColumn();
							this.endRecord();
						}

						this.lastLetter = var1;
					} else if (this.useCustomRecordDelimiter || var1 != '\r' && var1 != '\n') {
						if (this.userSettings.UseComments && this.columnsCount == 0 && var1 == this.userSettings.Comment) {
							this.lastLetter = var1;
							this.skipLine();
						} else if (this.userSettings.TrimWhitespace && (var1 == ' ' || var1 == '\t')) {
							this.startedColumn = true;
							this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
						} else {
							this.startedColumn = true;
							this.dataBuffer.ColumnStart = this.dataBuffer.Position;
							var2 = false;
							boolean var3 = false;
							byte var4 = 1;
							int var5 = 0;
							char var6 = 0;
							boolean var7 = true;

							do {
								if (!var7 && this.dataBuffer.Position == this.dataBuffer.Count) {
									this.checkDataLength();
								} else {
									if (!var7) {
										var1 = this.dataBuffer.Buffer[this.dataBuffer.Position];
									}

									if (!this.userSettings.UseTextQualifier && this.userSettings.EscapeMode == 2 && var1 == '\\') {
										if (var2) {
											var2 = false;
										} else {
											this.updateCurrentValue();
											var2 = true;
										}
									} else if (var3) {
										++var5;
										switch(var4) {
											case 1:
												var6 = (char)(var6 * 16);
												var6 += hexToDec(var1);
												if (var5 == 4) {
													var3 = false;
												}
												break;
											case 2:
												var6 = (char)(var6 * 8);
												var6 += (char)(var1 - 48);
												if (var5 == 3) {
													var3 = false;
												}
												break;
											case 3:
												var6 = (char)(var6 * 10);
												var6 += (char)(var1 - 48);
												if (var5 == 3) {
													var3 = false;
												}
												break;
											case 4:
												var6 = (char)(var6 * 16);
												var6 += hexToDec(var1);
												if (var5 == 2) {
													var3 = false;
												}
										}

										if (!var3) {
											this.appendLetter(var6);
										} else {
											this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
										}
									} else if (this.userSettings.EscapeMode == 2 && var2) {
										switch(var1) {
											case '0':
											case '1':
											case '2':
											case '3':
											case '4':
											case '5':
											case '6':
											case '7':
												var4 = 2;
												var3 = true;
												var5 = 1;
												var6 = (char)(var1 - 48);
												this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
											case '8':
											case '9':
											case ':':
											case ';':
											case '<':
											case '=':
											case '>':
											case '?':
											case '@':
											case 'A':
											case 'B':
											case 'C':
											case 'E':
											case 'F':
											case 'G':
											case 'H':
											case 'I':
											case 'J':
											case 'K':
											case 'L':
											case 'M':
											case 'N':
											case 'P':
											case 'Q':
											case 'R':
											case 'S':
											case 'T':
											case 'V':
											case 'W':
											case 'Y':
											case 'Z':
											case '[':
											case '\\':
											case ']':
											case '^':
											case '_':
											case '`':
											case 'c':
											case 'g':
											case 'h':
											case 'i':
											case 'j':
											case 'k':
											case 'l':
											case 'm':
											case 'p':
											case 'q':
											case 's':
											case 'w':
											default:
												break;
											case 'D':
											case 'O':
											case 'U':
											case 'X':
											case 'd':
											case 'o':
											case 'u':
											case 'x':
												switch(var1) {
													case 'D':
													case 'd':
														var4 = 3;
														break;
													case 'O':
													case 'o':
														var4 = 2;
														break;
													case 'U':
													case 'u':
														var4 = 1;
														break;
													case 'X':
													case 'x':
														var4 = 4;
												}

												var3 = true;
												var5 = 0;
												var6 = 0;
												this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
												break;
											case 'a':
												this.appendLetter('\u0007');
												break;
											case 'b':
												this.appendLetter('\b');
												break;
											case 'e':
												this.appendLetter('\u001b');
												break;
											case 'f':
												this.appendLetter('\f');
												break;
											case 'n':
												this.appendLetter('\n');
												break;
											case 'r':
												this.appendLetter('\r');
												break;
											case 't':
												this.appendLetter('\t');
												break;
											case 'v':
												this.appendLetter('\u000b');
										}

										var2 = false;
									} else if (var1 == this.userSettings.Delimiter) {
										this.endColumn();
									} else if (!this.useCustomRecordDelimiter && (var1 == '\r' || var1 == '\n') || this.useCustomRecordDelimiter && var1 == this.userSettings.RecordDelimiter) {
										this.endColumn();
										this.endRecord();
									}

									this.lastLetter = var1;
									var7 = false;
									if (this.startedColumn) {
										++this.dataBuffer.Position;
										if (this.userSettings.SafetySwitch && this.dataBuffer.Position - this.dataBuffer.ColumnStart + this.columnBuffer.Position > 100000) {
											this.close();
											throw new IOException("Maximum column length of 100,000 exceeded in column " + NumberFormat.getIntegerInstance().format((long)this.columnsCount) + " in record " + NumberFormat.getIntegerInstance().format(this.currentRecord) + ". Set the SafetySwitch property to false" + " if you're expecting column lengths greater than 100,000 characters to" + " avoid this error.");
										}
									}
								}
							} while(this.hasMoreData && this.startedColumn);
						}
					} else {
						if (!this.startedColumn && this.columnsCount <= 0 && (this.userSettings.SkipEmptyRecords || var1 != '\r' && this.lastLetter == '\r')) {
							this.dataBuffer.LineStart = this.dataBuffer.Position + 1;
						} else {
							this.endColumn();
							this.endRecord();
						}

						this.lastLetter = var1;
					}

					if (this.hasMoreData) {
						++this.dataBuffer.Position;
					}
				}

				if (!this.hasMoreData || this.hasReadNextLine) {
					if (this.startedColumn || this.lastLetter == this.userSettings.Delimiter) {
						this.endColumn();
						this.endRecord();
					}
					break;
				}
			}
		}

		if (this.userSettings.CaptureRawRecord) {
			if (this.hasMoreData) {
				if (this.rawBuffer.Position == 0) {
					this.rawRecord = new String(this.dataBuffer.Buffer, this.dataBuffer.LineStart, this.dataBuffer.Position - this.dataBuffer.LineStart - 1);
				} else {
					this.rawRecord = new String(this.rawBuffer.Buffer, 0, this.rawBuffer.Position) + new String(this.dataBuffer.Buffer, this.dataBuffer.LineStart, this.dataBuffer.Position - this.dataBuffer.LineStart - 1);
				}
			} else {
				this.rawRecord = new String(this.rawBuffer.Buffer, 0, this.rawBuffer.Position);
			}
		} else {
			this.rawRecord = "";
		}

		return this.hasReadNextLine;
	}

	private void checkDataLength() throws IOException {
		if (!this.initialized) {
			if (this.fileName != null) {
				this.inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(this.fileName), this.charset), 4096);
			}

			this.charset = null;
			this.initialized = true;
		}

		this.updateCurrentValue();
		if (this.userSettings.CaptureRawRecord && this.dataBuffer.Count > 0) {
			if (this.rawBuffer.Buffer.length - this.rawBuffer.Position < this.dataBuffer.Count - this.dataBuffer.LineStart) {
				int var1 = this.rawBuffer.Buffer.length + Math.max(this.dataBuffer.Count - this.dataBuffer.LineStart, this.rawBuffer.Buffer.length);
				char[] var2 = new char[var1];
				System.arraycopy(this.rawBuffer.Buffer, 0, var2, 0, this.rawBuffer.Position);
				this.rawBuffer.Buffer = var2;
			}

			System.arraycopy(this.dataBuffer.Buffer, this.dataBuffer.LineStart, this.rawBuffer.Buffer, this.rawBuffer.Position, this.dataBuffer.Count - this.dataBuffer.LineStart);
			this.rawBuffer.Position += this.dataBuffer.Count - this.dataBuffer.LineStart;
		}

		try {
			this.dataBuffer.Count = this.inputStream.read(this.dataBuffer.Buffer, 0, this.dataBuffer.Buffer.length);
		} catch (IOException var3) {
			this.close();
			throw var3;
		}

		if (this.dataBuffer.Count == -1) {
			this.hasMoreData = false;
		}

		this.dataBuffer.Position = 0;
		this.dataBuffer.LineStart = 0;
		this.dataBuffer.ColumnStart = 0;
	}

	public boolean readHeaders() throws IOException {
		boolean var1 = this.readRecord();
		this.headersHolder.Length = this.columnsCount;
		this.headersHolder.Headers = new String[this.columnsCount];

		for(int var2 = 0; var2 < this.headersHolder.Length; ++var2) {
			String var3 = this.get(var2);
			this.headersHolder.Headers[var2] = var3;
			this.headersHolder.IndexByName.put(var3, new Integer(var2));
		}

		if (var1) {
			--this.currentRecord;
		}

		this.columnsCount = 0;
		return var1;
	}

	public String getHeader(int var1) throws IOException {
		this.checkClosed();
		return var1 > -1 && var1 < this.headersHolder.Length ? this.headersHolder.Headers[var1] : "";
	}

	public boolean isQualified(int var1) throws IOException {
		this.checkClosed();
		return var1 < this.columnsCount && var1 > -1 ? this.isQualified[var1] : false;
	}

	private void endColumn() throws IOException {
		String var1 = "";
		int var2;
		if (this.startedColumn) {
			if (this.columnBuffer.Position == 0) {
				if (this.dataBuffer.ColumnStart < this.dataBuffer.Position) {
					var2 = this.dataBuffer.Position - 1;
					if (this.userSettings.TrimWhitespace && !this.startedWithQualifier) {
						while(var2 >= this.dataBuffer.ColumnStart && (this.dataBuffer.Buffer[var2] == ' ' || this.dataBuffer.Buffer[var2] == '\t')) {
							--var2;
						}
					}

					var1 = new String(this.dataBuffer.Buffer, this.dataBuffer.ColumnStart, var2 - this.dataBuffer.ColumnStart + 1);
				}
			} else {
				this.updateCurrentValue();
				var2 = this.columnBuffer.Position - 1;
				if (this.userSettings.TrimWhitespace && !this.startedWithQualifier) {
					while(var2 >= 0 && (this.columnBuffer.Buffer[var2] == ' ' || this.columnBuffer.Buffer[var2] == ' ')) {
						--var2;
					}
				}

				var1 = new String(this.columnBuffer.Buffer, 0, var2 + 1);
			}
		}

		this.columnBuffer.Position = 0;
		this.startedColumn = false;
		if (this.columnsCount >= 100000 && this.userSettings.SafetySwitch) {
			this.close();
			throw new IOException("Maximum column count of 100,000 exceeded in record " + NumberFormat.getIntegerInstance().format(this.currentRecord) + ". Set the SafetySwitch property to false" + " if you're expecting more than 100,000 columns per record to" + " avoid this error.");
		} else {
			if (this.columnsCount == this.values.length) {
				var2 = this.values.length * 2;
				String[] var3 = new String[var2];
				System.arraycopy(this.values, 0, var3, 0, this.values.length);
				this.values = var3;
				boolean[] var4 = new boolean[var2];
				System.arraycopy(this.isQualified, 0, var4, 0, this.isQualified.length);
				this.isQualified = var4;
			}

			this.values[this.columnsCount] = var1;
			this.isQualified[this.columnsCount] = this.startedWithQualifier;
			var1 = "";
			++this.columnsCount;
		}
	}

	private void appendLetter(char var1) {
		if (this.columnBuffer.Position == this.columnBuffer.Buffer.length) {
			int var2 = this.columnBuffer.Buffer.length * 2;
			char[] var3 = new char[var2];
			System.arraycopy(this.columnBuffer.Buffer, 0, var3, 0, this.columnBuffer.Position);
			this.columnBuffer.Buffer = var3;
		}

		this.columnBuffer.Buffer[this.columnBuffer.Position++] = var1;
		this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
	}

	private void updateCurrentValue() {
		if (this.startedColumn && this.dataBuffer.ColumnStart < this.dataBuffer.Position) {
			if (this.columnBuffer.Buffer.length - this.columnBuffer.Position < this.dataBuffer.Position - this.dataBuffer.ColumnStart) {
				int var1 = this.columnBuffer.Buffer.length + Math.max(this.dataBuffer.Position - this.dataBuffer.ColumnStart, this.columnBuffer.Buffer.length);
				char[] var2 = new char[var1];
				System.arraycopy(this.columnBuffer.Buffer, 0, var2, 0, this.columnBuffer.Position);
				this.columnBuffer.Buffer = var2;
			}

			System.arraycopy(this.dataBuffer.Buffer, this.dataBuffer.ColumnStart, this.columnBuffer.Buffer, this.columnBuffer.Position, this.dataBuffer.Position - this.dataBuffer.ColumnStart);
			this.columnBuffer.Position += this.dataBuffer.Position - this.dataBuffer.ColumnStart;
		}

		this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
	}

	private void endRecord() throws IOException {
		this.hasReadNextLine = true;
		++this.currentRecord;
	}

	public int getIndex(String var1) throws IOException {
		this.checkClosed();
		Object var2 = this.headersHolder.IndexByName.get(var1);
		return var2 != null ? (Integer)var2 : -1;
	}

	public boolean skipRecord() throws IOException {
		this.checkClosed();
		boolean var1 = false;
		if (this.hasMoreData) {
			var1 = this.readRecord();
			if (var1) {
				--this.currentRecord;
			}
		}

		return var1;
	}

	public boolean skipLine() throws IOException {
		this.checkClosed();
		this.columnsCount = 0;
		boolean var1 = false;
		if (this.hasMoreData) {
			boolean var2 = false;

			do {
				if (this.dataBuffer.Position == this.dataBuffer.Count) {
					this.checkDataLength();
				} else {
					var1 = true;
					char var3 = this.dataBuffer.Buffer[this.dataBuffer.Position];
					if (var3 == '\r' || var3 == '\n') {
						var2 = true;
					}

					this.lastLetter = var3;
					if (!var2) {
						++this.dataBuffer.Position;
					}
				}
			} while(this.hasMoreData && !var2);

			this.columnBuffer.Position = 0;
			this.dataBuffer.LineStart = this.dataBuffer.Position + 1;
		}

		this.rawBuffer.Position = 0;
		this.rawRecord = "";
		return var1;
	}

	public void close() {
		if (!this.closed) {
			this.close(true);
			this.closed = true;
		}

	}

	private void close(boolean var1) {
		if (!this.closed) {
			if (var1) {
				this.charset = null;
				this.headersHolder.Headers = null;
				this.headersHolder.IndexByName = null;
				this.dataBuffer.Buffer = null;
				this.columnBuffer.Buffer = null;
				this.rawBuffer.Buffer = null;
			}

			try {
				if (this.initialized) {
					this.inputStream.close();
				}
			} catch (Exception var3) {
				;
			}

			this.inputStream = null;
			this.closed = true;
		}

	}

	private void checkClosed() throws IOException {
		if (this.closed) {
			throw new IOException("This instance of the CsvReader class has already been closed.");
		}
	}

	protected void finalize() {
		this.close(false);
	}

	private static char hexToDec(char var0) {
		char var1;
		if (var0 >= 'a') {
			var1 = (char)(var0 - 97 + 10);
		} else if (var0 >= 'A') {
			var1 = (char)(var0 - 65 + 10);
		} else {
			var1 = (char)(var0 - 48);
		}

		return var1;
	}

	private class StaticSettings {
		public static final int MAX_BUFFER_SIZE = 1024;
		public static final int MAX_FILE_BUFFER_SIZE = 4096;
		public static final int INITIAL_COLUMN_COUNT = 10;
		public static final int INITIAL_COLUMN_BUFFER_SIZE = 50;

		private StaticSettings() {
		}
	}

	private class HeadersHolder {
		public String[] Headers = null;
		public int Length = 0;
		public HashMap IndexByName = new HashMap();

		public HeadersHolder() {
		}
	}

	private class UserSettings {
		public boolean CaseSensitive = true;
		public char TextQualifier = '"';
		public boolean TrimWhitespace = true;
		public boolean UseTextQualifier = true;
		public char Delimiter = ',';
		public char RecordDelimiter = 0;
		public char Comment = '#';
		public boolean UseComments = false;
		public int EscapeMode = 1;
		public boolean SafetySwitch = true;
		public boolean SkipEmptyRecords = true;
		public boolean CaptureRawRecord = true;

		public UserSettings() {
		}
	}

	private class Letters {
		public static final char LF = '\n';
		public static final char CR = '\r';
		public static final char QUOTE = '"';
		public static final char COMMA = ',';
		public static final char SPACE = ' ';
		public static final char TAB = '\t';
		public static final char POUND = '#';
		public static final char BACKSLASH = '\\';
		public static final char NULL = '\u0000';
		public static final char BACKSPACE = '\b';
		public static final char FORM_FEED = '\f';
		public static final char ESCAPE = '\u001b';
		public static final char VERTICAL_TAB = '\u000b';
		public static final char ALERT = '\u0007';

		private Letters() {
		}
	}

	private class RawRecordBuffer {
		public char[] Buffer = new char[500];
		public int Position = 0;

		public RawRecordBuffer() {
		}
	}

	private class ColumnBuffer {
		public char[] Buffer = new char[50];
		public int Position = 0;

		public ColumnBuffer() {
		}
	}

	private class DataBuffer {
		public char[] Buffer = new char[1024];
		public int Position = 0;
		public int Count = 0;
		public int ColumnStart = 0;
		public int LineStart = 0;

		public DataBuffer() {
		}
	}

	private class ComplexEscape {
		private static final int UNICODE = 1;
		private static final int OCTAL = 2;
		private static final int DECIMAL = 3;
		private static final int HEX = 4;

		private ComplexEscape() {
		}
	}
}
