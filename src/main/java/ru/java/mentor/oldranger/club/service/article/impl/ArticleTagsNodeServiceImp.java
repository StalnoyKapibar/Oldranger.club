package ru.java.mentor.oldranger.club.service.article.impl;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleTagsNodeRepository;
import ru.java.mentor.oldranger.club.dto.ArticleAndCommentsDto;
import ru.java.mentor.oldranger.club.dto.ArticleTagsNodeDto;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;
import ru.java.mentor.oldranger.club.service.article.ArticleTagsNodeService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArticleTagsNodeServiceImp implements ArticleTagsNodeService {

    private ArticleTagsNodeRepository tagsNodeRepository;

    @Override
    public List<ArticleTagsNode> findAll() {
        return tagsNodeRepository.findAll();
    }

    @Override
    public List<ArticleTagsNodeDto> findHierarchyTreeOfAllTagsNodes() {
        return tagsNodeRepository.getAllTagsNodesTree().stream().map(e -> new ArticleTagsNodeDto(e.getId(),
                e.getParent() == null ? -1L : e.getParent().getId(),
                e.getTag(),
                e.getTagsHierarchy())
        ).collect(Collectors.toList());
    }

    @Override
    public ArticleTagsNode findById(Long id) {
        return tagsNodeRepository.findById(id).orElse(null);
    }

    @Override
    public void save(ArticleTagsNode tagsNode) {
        tagsNodeRepository.save(tagsNode);
    }

    @Override
    public void delete(ArticleTagsNode tagsNode) {
        tagsNodeRepository.delete(tagsNode);
    }

    @Override
    public void deleteById(Long id) {
        tagsNodeRepository.deleteById(id);
    }

    @Override
    public Set<ArticleTagsNode> findByIdIn(List<Long> tagsNodes) {
        return tagsNodeRepository.findByIdIn(tagsNodes);
    }
}
