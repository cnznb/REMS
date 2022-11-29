package br.ufmg.dcc.labsoft.jextract.model.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;

import br.ufmg.dcc.labsoft.jextract.model.MethodModel;
import br.ufmg.dcc.labsoft.jextract.ranking.Utils;

public class MethodModelBuilder extends ASTVisitor {

	private LinkedHashMap<Object, StatementImpl> statementsMap;
	private List<BlockImpl> blocks;
	private BlockBasedPdg pdg;
	private MethodDeclaration methodDeclaration;

	private MethodModelBuilder() {
		// private constructor
	}

	public static MethodModel create(ICompilationUnit src, MethodDeclaration methodDeclaration) {
		return new MethodModelBuilder().getModel(src, methodDeclaration);
	}

	public MethodModel getModel(ICompilationUnit src, MethodDeclaration methodDeclaration) {
		this.methodDeclaration = methodDeclaration;
		this.statementsMap = new LinkedHashMap<Object, StatementImpl>();
		this.blocks = new ArrayList<BlockImpl>();
		this.pdg = new BlockBasedPdg();
		
		methodDeclaration.accept(this);
		
		this.pdg.build(methodDeclaration, this.statementsMap, this.blocks);
		
		BlockImpl[] ba = this.blocks.toArray(new BlockImpl[this.blocks.size()]);
		MethodModelImpl methodModel = new MethodModelImpl(src, methodDeclaration, ba);
		
		return methodModel;
	}

	@Override
	public void preVisit(ASTNode node) {
		if (node instanceof Statement) {
			Statement node1 = (Statement) node;
			// O pai direto de um statement pode n�o ser um statement quando existe inner class na jogada.
			StatementImpl parent = this.statementsMap.get(Utils.findEnclosingStatement(node1.getParent()));
			boolean blockLike = node1 instanceof Block || node1 instanceof SwitchStatement;
			StatementImpl emrStatement = new StatementImpl(this.statementsMap.size(), node1, parent, blockLike);
			this.statementsMap.put(node1, emrStatement);
		}
	}

	@Override
	public void postVisit(ASTNode node) {
		if (node instanceof Statement) {
			Statement stmNode = (Statement) node;
			final StatementImpl thisStatement = this.statementsMap.get(stmNode);
			if (node instanceof Block) {
				// Creates a block with all children.
				createBlock(thisStatement, ((Block) node).statements());
			} else if (node instanceof SwitchStatement) {
				@SuppressWarnings("unchecked")
                List<Statement> children = ((SwitchStatement) node).statements();
				for (int i = 0, length = children.size(); i < length;) {
					for (; i < length && children.get(i) instanceof SwitchCase; i++);
					List<Statement> statementsOfSwitchCase = new ArrayList<Statement>();
					for (; i < length && !(children.get(i) instanceof SwitchCase); i++) {
						statementsOfSwitchCase.add(children.get(i));
					}
					createBlock(thisStatement, statementsOfSwitchCase);
				}
			} else if (!thisStatement.isBlock() && !thisStatement.parent.isBlock()) {
				// Creates a block with a single statement.
				createVirtualBlock(thisStatement);
			}
			//fillEntities(stmNode, thisStatement);
		}
	}

//	private void fillEntities(final ASTNode stmNode, final StatementImpl thisStatement) {
//		stmNode.accept(new DependenciesAstVisitor(this.methodDeclaration.resolveBinding().getDeclaringClass()) {
//	    	@Override
//	    	public void onModuleAccess(ASTNode node, String packageName) {
//    			thisStatement.getEntitiesP().add(packageName);
//	    	}
//	    	@Override
//	    	public void onTypeAccess(ASTNode node, ITypeBinding binding) {
//    			thisStatement.getEntitiesT().add(binding.getKey());
//	    	}
//	    	@Override
//	    	public void onVariableAccess(ASTNode node, IVariableBinding binding) {
//    			thisStatement.getEntitiesV().add(binding.getKey());
//	    	}
//	    	// Override preVisit2 to avoid visiting children statements.
//	    	@Override
//	    	public boolean preVisit2(ASTNode node) {
//	    		if (node instanceof Statement && node != stmNode) {
//	    			return false;
//	    		}
//	    	    return super.preVisit2(node);
//	    	}
//	    });
//    }

	private void createBlock(StatementImpl thisStatement, @SuppressWarnings("rawtypes") List statements) {
		BlockImpl emrBlock = new BlockImpl(this.blocks.size(), thisStatement, this.pdg);
		for (Object stm : statements) {
			boolean isBreakOrContinue = stm instanceof BreakStatement || stm instanceof ContinueStatement;
			if (!isBreakOrContinue) {
				StatementImpl statement = this.statementsMap.get(stm);
				emrBlock.appendStatement(statement);
			}
		}
		this.blocks.add(emrBlock);
	}

	private void createVirtualBlock(StatementImpl thisStatement) {
		BlockImpl emrBlock = new BlockImpl(this.blocks.size(), thisStatement, this.pdg);
		emrBlock.appendStatement(thisStatement);
		this.blocks.add(emrBlock);
	}

}
