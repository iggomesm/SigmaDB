package br.com.sigmadb.enumerations;

public enum EnumSortType {
	ASC(1), DESC(-1);

	private int index = 1;

	private EnumSortType(int index) {
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}
}
