package ru.java.mentor.oldranger.club.service.article.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleTagsNodeRepository;
import ru.java.mentor.oldranger.club.dto.ArticleTagsNodeDto;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;
import ru.java.mentor.oldranger.club.service.article.ArticleTagsNodeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ArticleTagsNodeServiceImp implements ArticleTagsNodeService {
    private ArticleTagsNodeRepository tagsNodeRepository;

    @Override
    public List<ArticleTagsNode> findAll() {
        return tagsNodeRepository.findAll();
    }

    @Override
    public List<ArticleTagsNodeDto> findHierarchyTreeOfAllTagsNodes() {
        return tagsNodeRepository.findAllChildrenTree().stream()
                .map(e -> new ArticleTagsNodeDto(e.getId(),
                        e.getParent() == null ? -1L : e.getParent().getId(),
                        e.getTag(),
                        Arrays.stream(e.getTagsHierarchy().split(",")).mapToInt(Integer::parseInt).toArray()))
                .collect(Collectors.toList());
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
    public void deleteById(Long id) {
        tagsNodeRepository.deleteByIdIn(tagsNodeRepository.findDescendantsAndParentIdsByParentId(id));
    }
}
