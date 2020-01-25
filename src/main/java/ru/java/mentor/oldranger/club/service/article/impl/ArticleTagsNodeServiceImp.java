package ru.java.mentor.oldranger.club.service.article.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleTagsNodeRepository;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;
import ru.java.mentor.oldranger.club.service.article.ArticleTagsNodeService;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ArticleTagsNodeServiceImp implements ArticleTagsNodeService {

    private ArticleTagsNodeRepository tagsNodeRepository;

    @Override
    public List<ArticleTagsNode> getAllTagsNodes() {
        return tagsNodeRepository.findAll();
    }

    @Override
    public List<ArticleTagsNode> getAllTagsNodesTree() {
        //ToDo
        return null;
    }

    @Override
    public ArticleTagsNode getTagsNodeById(Long id) {
        return tagsNodeRepository.findById(id).orElse(null);
    }

    @Override
    public void addTagsNode(ArticleTagsNode tagsNode) {
        tagsNodeRepository.save(tagsNode);
    }

    @Override
    public void updateArticleTagsNode(ArticleTagsNode tagsNode) {
        tagsNodeRepository.save(tagsNode);
    }

    @Override
    public void deleteArticleTagsNode(ArticleTagsNode tagsNode) {
        tagsNodeRepository.delete(tagsNode);
    }

    @Override
    public Set<ArticleTagsNode> addArticleTagsNodeToSet(List<Long> tagsNodes) {
        return tagsNodeRepository.findByIdIn(tagsNodes);
    }
}
