package br.ufmg.dcc.labsoft.jextract.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;

import br.ufmg.dcc.labsoft.jextract.model.BlockModel;
import br.ufmg.dcc.labsoft.jextract.model.EntitySet;
import br.ufmg.dcc.labsoft.jextract.model.StatementModel;


class StatementImpl implements StatementModel {

	private int index = 0;
	private int indexInBlock = 0;
	private int selfSize = 0;
	private int childrenSize = 0;
	private boolean block = false;
	StatementImpl parent;
	MethodModelImpl methodModel;
	Statement astNode = null;
	BlockModel parentBlock = null;
	private final List<StatementImpl> children = new ArrayList<StatementImpl>();

	private final EntitySet entitiesP = new EntitySet();
	private final EntitySet entitiesT = new EntitySet();
	private final EntitySet entitiesV = new EntitySet();

	private static final StatementImpl NIL = new StatementImpl(){
		@Override
		void registerAsChild(StatementImpl child) {};
		@Override
		void increaseSize(int size) {};
	};

	private StatementImpl() {
		this.index = -1;
		this.selfSize = 0;
		this.childrenSize = 0;
		this.parent = this;
	}

	StatementImpl(int index, Statement astNode, StatementImpl parent, boolean block) {
		this.index = index;
		this.selfSize = astNode instanceof Block ? 0 : 1;
		this.childrenSize = 0;
		this.parent = parent != null ? parent : NIL;
		this.parent.registerAsChild(this);
		this.block = block;
		this.astNode = astNode;
	}

	void registerAsChild(StatementImpl child) {
		this.children.add(child);
		this.increaseSize(child.getTotalSize());
	}

	@Override
	public List<? extends StatementModel> getDescendents() {
		ArrayList<StatementImpl> result = new ArrayList<StatementImpl>();
		this.fillDescendentsRecursive(result);
		return result;
	}
	
	private void fillDescendentsRecursive(List<StatementImpl> list) {
		for (StatementImpl child : this.children) {
			list.add(child);
			child.fillDescendentsRecursive(list);
		}
	}
	
	void increaseSize(int size) {
		this.childrenSize += size;
		this.parent.increaseSize(size);
	}

	@Override
	public int getIndexInBlock() {
		return this.indexInBlock;
	}

	void setIndexInBlock(int indexInBlock) {
		this.indexInBlock = indexInBlock;
	}

	public int getIndex() {
		return this.index;
	}

	@Override
	public int getTotalSize() {
		return this.selfSize + this.childrenSize;
	}

	@Override
	public BlockModel getParentBlock() {
		return this.parentBlock;
	}

	public boolean isBlock() {
		return this.block;
	}

	@Override
	public Statement getAstNode() {
		return this.astNode;
	}

	public void setParentBlock(BlockModel parentBlock) {
		this.parentBlock = parentBlock;
	}

	@Override
	public EntitySet getEntitiesP() {
		return entitiesP;
	}

	@Override
	public EntitySet getEntitiesT() {
		return entitiesT;
	}

	@Override
	public EntitySet getEntitiesV() {
		return entitiesV;
	}

	@Override
	public String toString() {
	    String src = this.astNode.toString();
	    if (src.indexOf('\n') == -1) {
	    	return src;
	    }
		String firstLine = src.substring(0, src.indexOf('\n'));
		if (src.length() > firstLine.length() + 1) {
			return firstLine + " ...";
		} else {
			return firstLine;
		}
	}

	public StatementModel getParentStatement() {
		return this.parent;
	}

}
