import gr.uom.java.xmi.decomposition.AbstractCodeFragment;
import gr.uom.java.xmi.decomposition.AbstractCodeMapping;
import gr.uom.java.xmi.decomposition.UMLOperationBodyMapper;
import gr.uom.java.xmi.decomposition.replacement.Replacement;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;


import java.util.List;
import java.util.Set;

public class clone_all {
    /**
     * 1.将所有与训练数据集相关的github项目clone下来存于自建项目的tmp目录下
     */
    public static void main(String args[]) throws Exception{
        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        Repository repo = gitService.cloneIfNotExists(
                "tmp/liferay-portal",
                "https://github.com/liferay/liferay-portal.git");

        miner.detectAtCommit(repo, "59fd9e696cec5f2ed44c27422bbc426b11647321", new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
                System.out.println("Refactorings at " + commitId);
                for (Refactoring ref : refactorings) {
                    System.out.println(ref.toJSON());
                }
            }
        });
    }
}
/**
 * c9b2006381301c99b66c50c4b31f329caac06137
 * ebb1c2c364e888d4a0f47abe691cb2bad4eb4e75
 * e58c9c3eef4c6e44b21a97cfbd2862bb2eb4627a
 * d47e58f9bbce9a816378e8a7930c1de67a864c29
 * ...
 */